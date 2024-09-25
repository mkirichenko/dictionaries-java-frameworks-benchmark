package ru.tbank.dictionaries.helidon;

import io.helidon.common.context.Contexts;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbStatementDml;
import io.helidon.dbclient.DbTransaction;
import io.helidon.http.BadRequestException;
import io.helidon.http.Status;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.util.ArrayList;
import java.util.List;

public class DictionariesService implements HttpService {

    private final DbClient dbClient;

    public DictionariesService() {
        Config config = Config.global().get("db");
        this.dbClient = Contexts.globalContext()
                .get(DbClient.class)
                .orElseGet(() -> DbClient.builder(config)
                        .mapperProvider(new DictionariesMapperProvider())
                        .build());
    }

    @Override
    public void afterStop() {
        HttpService.super.afterStop();
        dbClient.close();
    }

    @Override
    public void routing(HttpRules rules) {
        rules
                .get("/dictionary", this::getAll)
                .get("/dictionary/{category}/{name}", this::get)
                .post("/dictionary", Handler.create(Dictionaries.class, this::create))
                .post("/dictionary/batch", Handler.create(DictionariesCreationBatchRequest.class, this::createBatch))
                .put("/dictionary/{category}/{name}", this::updateViaPath)
                .put("/dictionary", this::updateViaQuery)
                .delete("/dictionary/{category}/{name}", this::deleteViaPath)
                .delete("/dictionary", this::deleteViaQuery);
    }

    private void getAll(ServerRequest request, ServerResponse response) {
        StringBuilder query = new StringBuilder("SELECT id, category, name, sorder, main_value, secondary_value "
                + "FROM table_dictionaries");
        List<String> conditions = new ArrayList<>();
        List<String> arguments = new ArrayList<>();
        if (request.query().first("category").isPresent()) {
            arguments.add(request.query().get("category"));
            conditions.add("category = ?");
        }
        if (request.query().first("name").isPresent()) {
            arguments.add(request.query().get("name"));
            conditions.add("name = ?");
        }
        if (request.query().first("mainValue").isPresent()) {
            arguments.add(request.query().get("mainValue"));
            conditions.add("main_value = ?");
        }
        if (request.query().first("secondaryValue").isPresent()) {
            arguments.add(request.query().get("secondaryValue"));
            conditions.add("secondary_value = ?");
        }

        if (!conditions.isEmpty()) {
            query.append(" WHERE ");
            query.append(String.join(" AND ", conditions));
        }
        query.append(" ORDER BY category, sorder");

        List<Dictionaries> dictionaries = dbClient.execute()
                .query(query.toString(), arguments.toArray())
                .map(row -> row.as(Dictionaries.class))
                .toList();

        response
                .header("Content-Type", "application/json;charset=UTF-8")
                .send(dictionaries);
    }

    private void get(ServerRequest request, ServerResponse response) {
        String category = request.path().pathParameters().get("category");
        String name = request.path().pathParameters().get("name");

        DictionariesValues dictionariesValues = dbClient.execute()
                .get("SELECT sorder, main_value, secondary_value "
                        + "FROM table_dictionaries "
                        + "WHERE category = ? AND name = ?", category, name)
                .orElseThrow(() -> new DictionariesNotFoundException(category, name))
                .as(DictionariesValues.class);

        response
                .header("Content-Type", "application/json;charset=UTF-8")
                .send(dictionariesValues);
    }

    private void create(Dictionaries request, ServerResponse response) {
        if (!DictionariesValidator.validate(request)) {
            throw new BadRequestException("invalid request");
        }

        DbTransaction transaction = dbClient.transaction();
        try {
            if (checkIfDictionaryExists(transaction, request.getCategory(), request.getName())) {
                throw new DictionariesAlreadyExistsException(request.getCategory(), request.getName());
            }

            transaction.createInsert(
                            "INSERT INTO table_dictionaries(id, category, name, sorder, main_value, secondary_value) "
                                    + "VALUES ((SELECT nextval('dictionaries_id_seq')), ?, ?, ?, ?, ?)")
                    .indexedParam(request)
                    .execute();
            transaction.commit();

            response.status(Status.OK_200).send();
        } catch (Throwable e) {
            transaction.rollback();
            throw e;
        }
    }

    private void createBatch(DictionariesCreationBatchRequest batchRequest, ServerResponse response) {
        if (!DictionariesValidator.validate(batchRequest)) {
            throw new BadRequestException("invalid request");
        }

        DbTransaction transaction = dbClient.transaction();
        try {
            for (Dictionaries request : batchRequest.getRequests()) {
                if (checkIfDictionaryExists(transaction, request.getCategory(), request.getName())) {
                    throw new DictionariesAlreadyExistsException(request.getCategory(), request.getName());
                }

                transaction.insert(
                        "INSERT INTO table_dictionaries(id, category, name, sorder, main_value, secondary_value) "
                                + "VALUES ((SELECT nextval('dictionaries_id_seq')), ?, ?, ?, ?, ?)", request);
            }

            transaction.commit();

            response.status(Status.OK_200).send();
        } catch (Throwable e) {
            transaction.rollback();
            throw e;
        }
    }

    private void updateViaPath(ServerRequest request, ServerResponse response) {
        String category = request.path().pathParameters().get("category");
        String name = request.path().pathParameters().get("name");
        DictionariesValues dictionaryValues = request.content().as(DictionariesValues.class);

        update(category, name, dictionaryValues, response);
    }

    private void updateViaQuery(ServerRequest request, ServerResponse response) {
        String category = request.query().get("category");
        String name = request.query().get("name");
        DictionariesValues dictionaryValues = request.content().as(DictionariesValues.class);

        update(category, name, dictionaryValues, response);
    }

    private void update(String category, String name, DictionariesValues dictionaryValues, ServerResponse response) {
        DbTransaction transaction = dbClient.transaction();
        try {
            StringBuilder query = new StringBuilder("UPDATE table_dictionaries SET ");

            ArrayList<String> setters = new ArrayList<>();
            ArrayList<Object> arguments = new ArrayList<>();
            if (dictionaryValues.getOrder() != null) {
                arguments.add(dictionaryValues.getOrder());
                setters.add("sorder = ?");
            }
            if (dictionaryValues.getMainValue() != null) {
                arguments.add(dictionaryValues.getMainValue());
                setters.add("main_value = ?");
            }
            if (dictionaryValues.getSecondaryValue() != null) {
                arguments.add(dictionaryValues.getSecondaryValue());
                setters.add("secondary_value = ?");
            }

            if (setters.isEmpty()) {
                if (!checkIfDictionaryExists(transaction, category, name)) {
                    throw new DictionariesNotFoundException(category, name);
                }
            }

            query.append(String.join(" , ", setters));
            query.append(" WHERE category = ?");
            query.append(" AND name = ?");

            DbStatementDml statement = transaction.createUpdate(query.toString());
            for (Object argument : arguments) {
                statement.addParam(argument);
            }
            statement.addParam(category);
            statement.addParam(name);

            long updated = statement.execute();
            transaction.commit();

            if (updated > 0) {
                response.status(Status.OK_200).send();
            } else {
                throw new DictionariesNotFoundException(category, name);
            }
        } catch (Throwable e) {
            transaction.rollback();
            throw e;
        }
    }

    private static boolean checkIfDictionaryExists(DbTransaction transaction, String category, String name) {
        return transaction
                .get("SELECT count(1) "
                        + "FROM table_dictionaries "
                        + "WHERE category = ? AND name = ?", category, name)
                .map(row -> row.column(1).getLong() > 0)
                .orElse(false);
    }

    private void deleteViaPath(ServerRequest request, ServerResponse response) {
        String category = request.path().pathParameters().get("category");
        String name = request.path().pathParameters().get("name");

        delete(category, name, response);
    }

    private void deleteViaQuery(ServerRequest request, ServerResponse response) {
        String category = request.query().get("category");
        String name = request.query().get("name");

        delete(category, name, response);
    }

    private void delete(String category, String name, ServerResponse response) {
        long deleted = dbClient.execute()
                .delete("DELETE FROM table_dictionaries "
                        + "WHERE category = ? AND name = ?", category, name);

        if (deleted > 0) {
            response.status(Status.OK_200).send();
        } else {
            throw new DictionariesNotFoundException(category, name);
        }
    }
}

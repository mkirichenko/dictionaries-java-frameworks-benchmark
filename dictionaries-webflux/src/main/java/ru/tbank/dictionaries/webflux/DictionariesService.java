package ru.tbank.dictionaries.webflux;

import static org.springframework.data.relational.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DictionariesService {

    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public DictionariesService(DatabaseClient databaseClient, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.databaseClient = databaseClient;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    public Mono<List<Dictionaries>> getAll(String category, String name, String mainValue, String secondaryValue) {

        StringBuilder query = new StringBuilder("SELECT id, category, name, sorder, main_value, secondary_value "
                + "FROM table_dictionaries");
        List<String> conditions = new ArrayList<>();
        List<String> arguments = new ArrayList<>();
        if (category != null) {
            arguments.add(category);
            conditions.add("category = $" + arguments.size());
        }
        if (name != null) {
            arguments.add(name);
            conditions.add("name = $" + arguments.size());
        }
        if (mainValue != null) {
            arguments.add(mainValue);
            conditions.add("main_value = $" + arguments.size());
        }
        if (secondaryValue != null) {
            arguments.add(secondaryValue);
            conditions.add("secondary_value = $" + arguments.size());
        }

        if (!conditions.isEmpty()) {
            query.append(" WHERE ");
            query.append(String.join(" AND ", conditions));
        }
        query.append(" ORDER BY category, sorder");

        GenericExecuteSpec selectSpec = databaseClient.sql(query.toString());
        for (int i = 0; i < arguments.size(); ++i) {
            selectSpec = selectSpec.bind(i, arguments.get(i));
        }
        return selectSpec
                .map(row -> new Dictionaries(
                        row.get("category", String.class),
                        row.get("name", String.class),
                        row.get("sorder", Integer.class),
                        row.get("main_value", String.class),
                        row.get("secondary_value", String.class)))
                .all()
                .collectList();
    }

    public Mono<DictionariesValues> get(String category, String name) {
        return r2dbcEntityTemplate.select(DictionariesEntity.class)
                .matching(Query.query(
                        where("category").is(category)
                                .and(where("name").is(name))))
                .first()
                .map(DictionariesService::entityToValues)
                .switchIfEmpty(Mono.error(() -> new DictionariesNotFoundException(category, name)));
    }

    @Transactional
    public Mono<Void> create(Dictionaries dictionaries) {
        return checkCategoryNameUnique(dictionaries.getCategory(), dictionaries.getName())
                .then(Mono.defer(() -> {
                    String sql =
                            "INSERT INTO table_dictionaries(id, category, name, sorder, main_value, secondary_value) "
                                    + "VALUES ((SELECT nextval('dictionaries_id_seq')), $1, $2, $3, $4, $5)";

                    GenericExecuteSpec insertSpec = databaseClient.sql(sql)
                            .bind(0, dictionaries.getCategory())
                            .bind(1, dictionaries.getName());

                    insertSpec = bindNullable(insertSpec, 2, dictionaries.getOrder(), Integer.class);
                    insertSpec = bindNullable(insertSpec, 3, dictionaries.getMainValue(), String.class);
                    insertSpec = bindNullable(insertSpec, 4, dictionaries.getSecondaryValue(), String.class);

                    return insertSpec
                            .fetch()
                            .rowsUpdated()
                            .then();
                }));
    }

    @Transactional
    public Mono<Void> createFromList(List<Dictionaries> requests) {
        ArrayList<Mono<Void>> createMonos = new ArrayList<>();
        for (Dictionaries Dictionaries : requests) {
            createMonos.add(create(Dictionaries));
        }
        return Flux.concat(createMonos)
                .then();
    }

    public Mono<Void> update(String category, String name, DictionariesValues dictionaryValues) {
        StringBuilder query = new StringBuilder("UPDATE table_dictionaries SET ");

        ArrayList<String> setters = new ArrayList<>();
        ArrayList<Object> arguments = new ArrayList<>();
        if (dictionaryValues.getOrder() != null) {
            arguments.add(dictionaryValues.getOrder());
            setters.add("sorder = $" + arguments.size());
        }
        if (dictionaryValues.getMainValue() != null) {
            arguments.add(dictionaryValues.getMainValue());
            setters.add("main_value = $" + arguments.size());
        }
        if (dictionaryValues.getSecondaryValue() != null) {
            arguments.add(dictionaryValues.getSecondaryValue());
            setters.add("secondary_value = $" + arguments.size());
        }

        if (setters.isEmpty()) {
            return r2dbcEntityTemplate.exists(Query.query(
                            where("category").is(category)
                                    .and(where("name").is(name))), DictionariesEntity.class)
                    .flatMap(exists -> {
                        if (exists) {
                            return Mono.empty();
                        } else {
                            return Mono.error(new DictionariesNotFoundException(category, name));
                        }
                    });
        }

        query.append(String.join(" , ", setters));
        query.append(" WHERE category = $").append(arguments.size() + 1);
        query.append(" AND name = $").append(arguments.size() + 2);

        GenericExecuteSpec updateSpec = databaseClient.sql(query.toString());
        for (int i = 0; i < arguments.size(); ++i) {
            updateSpec = updateSpec.bind(i, arguments.get(i));
        }
        return updateSpec
                .bind(arguments.size(), category)
                .bind(arguments.size() + 1, name)
                .fetch()
                .rowsUpdated()
                .flatMap(updated -> {
                    if (updated == 0) {
                        return Mono.error(new DictionariesNotFoundException(category, name));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    public Mono<Void> delete(String category, String name) {
        return r2dbcEntityTemplate
                .delete(DictionariesEntity.class)
                .matching(Query.query(where("category").is(category).and(where("name").is(name))))
                .all()
                .flatMap(deleted -> {
                    if (deleted == 0) {
                        return Mono.error(new DictionariesNotFoundException(category, name));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    private Mono<Void> checkCategoryNameUnique(String category, String name) {
        return r2dbcEntityTemplate.exists(Query.query(
                        where("category").is(category)
                                .and("name").is(name)), DictionariesEntity.class)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DictionariesAlreadyExistsException(category, name));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    private static GenericExecuteSpec bindNullable(GenericExecuteSpec spec, int index, Object value, Class<?> type) {
        if (value != null) {
            return spec.bind(index, value);
        } else {
            return spec.bindNull(index, type);
        }
    }

    private static DictionariesValues entityToValues(DictionariesEntity entity) {
        return new DictionariesValues(entity.getOrder(), entity.getMainValue(), entity.getSecondaryValue());
    }
}

package ru.tbank.dictionaries.kora;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import ru.tinkoff.kora.common.Component;
import ru.tinkoff.kora.database.common.UpdateCount;
import ru.tinkoff.kora.database.jdbc.mapper.result.JdbcResultSetMapper;

@Component
public final class DictionariesService {

    private final DictionariesRepository repository;
    private final JdbcResultSetMapper<List<DictionariesEntity>> entityListResultSetMapper;

    public DictionariesService(DictionariesRepository repository, JdbcResultSetMapper<List<DictionariesEntity>> entityListResultSetMapper) {
        this.repository = repository;
        this.entityListResultSetMapper = entityListResultSetMapper;
    }

    public List<Dictionaries> getAll(String category, String name, String mainValue, String secondaryValue) {

        StringBuilder query = new StringBuilder("SELECT id, category, name, sorder, main_value, secondary_value "
                + "FROM table_dictionaries");
        List<String> conditions = new ArrayList<>();
        List<String> arguments = new ArrayList<>();
        if (category != null) {
            conditions.add("category = ?");
            arguments.add(category);
        }
        if (name != null) {
            conditions.add("name = ?");
            arguments.add(name);
        }
        if (mainValue != null) {
            conditions.add("main_value = ?");
            arguments.add(mainValue);
        }
        if (secondaryValue != null) {
            conditions.add("secondary_value = ?");
            arguments.add(secondaryValue);
        }

        if (!conditions.isEmpty()) {
            query.append(" WHERE ");
            query.append(String.join(" AND ", conditions));
        }
        query.append(" ORDER BY category, sorder");

        return repository
                .getJdbcConnectionFactory().withConnection(connection -> {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
                        for (int i = 0; i < arguments.size(); ++i) {
                            preparedStatement.setString(i + 1, arguments.get(i));
                        }
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            return entityListResultSetMapper.apply(resultSet);
                        }
                    }
                })
                .stream()
                .map(DictionariesService::entityToResponse)
                .toList();
    }

    public Dictionaries get(String category, String name) {
        DictionariesEntity entity = repository.findByCategoryAndName(category, name);
        if (entity == null) {
            throw new DictionariesNotFoundException(category, name);
        }
        return entityToResponse(entity);
    }

    public void create(Dictionaries request) {
        repository.getJdbcConnectionFactory().inTx(() -> {
            checkIfDictionariesExists(request);
            repository.insert(entityFromRequest(request));
        });
    }

    public void createFromList(List<Dictionaries> requests) {
        repository.getJdbcConnectionFactory().inTx(() -> {
            for (Dictionaries request : requests) {
                checkIfDictionariesExists(request);
                repository.insert(entityFromRequest(request));
            }
        });
    }

    public void update(String category, String name, DictionariesValues request) {
        UpdateCount updated = repository.updateByCategoryAndName(category, name, entityFromValuesRequest(request));
        if (updated.value() == 0) {
            throw new DictionariesNotFoundException(category, name);
        }
    }

    public void delete(String category, String name) {
        UpdateCount deleted = repository.deleteByCategoryAndName(category, name);
        if (deleted.value() == 0) {
            throw new DictionariesNotFoundException(category, name);
        }
    }

    private void checkIfDictionariesExists(Dictionaries request) {
        if (repository.existByCategoryAndName(request.getCategory(), request.getName()) > 0) {
            throw new DictionariesAlreadyExistsException(request.getCategory(), request.getName());
        }
    }

    private static Dictionaries entityToResponse(DictionariesEntity entity) {
        return new Dictionaries(entity.category(), entity.name(), entity.order(),
                entity.mainValue(), entity.secondaryValue());
    }

    private static DictionariesEntity entityFromRequest(Dictionaries request) {
        return new DictionariesEntity(0, request.getCategory(), request.getName(), request.getOrder(),
                request.getMainValue(), request.getSecondaryValue());
    }

    private static DictionariesEntity entityFromValuesRequest(DictionariesValues request) {
        return new DictionariesEntity(0, null, null, request.getOrder(),
                request.getMainValue(), request.getSecondaryValue());
    }
}

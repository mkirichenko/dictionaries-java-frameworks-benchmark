package ru.tbank.dictionaries.springtomcatvt;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.asm.Type;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DictionariesService {

    private static final DictionariesValuesRowMapper ROW_MAPPER = new DictionariesValuesRowMapper();

    private final DictionariesRepository repository;
    private final JdbcTemplate jdbcTemplate;

    public DictionariesService(DictionariesRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
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

        return jdbcTemplate.query(query.toString(), preparedStatement -> {
            for (int i = 0; i < arguments.size(); ++i) {
                preparedStatement.setString(i + 1, arguments.get(i));
            }
        }, ROW_MAPPER);
    }

    @Transactional(readOnly = true)
    public DictionariesValues get(String category, String name) {
        DictionariesEntity entity = findEntity(category, name);
        return entityToValues(entity);
    }

    @Transactional
    public void create(Dictionaries dictionaries) {
        checkCategoryNameUnique(dictionaries.getCategory(), dictionaries.getName());

        String sql = "INSERT INTO table_dictionaries(id, category, name, sorder, main_value, secondary_value) "
                + "VALUES ((SELECT nextval('dictionaries_id_seq')), ?, ?, ?, ?, ?)";
        jdbcTemplate.execute(sql, (PreparedStatement p) -> {
            p.setString(1, dictionaries.getCategory());
            p.setString(2, dictionaries.getName());
            if (dictionaries.getOrder() != null) {
                p.setInt(3, dictionaries.getOrder());
            } else {
                p.setNull(3, Type.INT);
            }
            p.setString(4, dictionaries.getMainValue());
            p.setString(5, dictionaries.getSecondaryValue());

            p.execute();
            return p.getUpdateCount();
        });
    }

    @Transactional
    public void createFromList(List<Dictionaries> requests) {
        for (Dictionaries dictionaries : requests) {
            create(dictionaries);
        }
    }

    @Transactional
    public void update(String category, String name, DictionariesValues dictionaryValues) {
        DictionariesEntity entity = findEntity(category, name);
        if (dictionaryValues.getOrder() != null) {
            entity.setOrder(dictionaryValues.getOrder());
        }
        if (dictionaryValues.getMainValue() != null) {
            entity.setMainValue(dictionaryValues.getMainValue());
        }
        if (dictionaryValues.getSecondaryValue() != null) {
            entity.setSecondaryValue(dictionaryValues.getSecondaryValue());
        }
        repository.save(entity);
    }

    @Transactional
    public void delete(String category, String name) {
        boolean deleted = repository.deleteByCategoryAndName(category, name);
        if (!deleted) {
            throw new DictionariesNotFoundException(category, name);
        }
    }

    private DictionariesEntity findEntity(String category, String name) {
        DictionariesEntity entity = repository.findByCategoryAndName(category, name);
        if (entity == null) {
            throw new DictionariesNotFoundException(category, name);
        }
        return entity;
    }

    private void checkCategoryNameUnique(String category, String name) {
        if (repository.existsByCategoryAndName(category, name)) {
            throw new DictionariesAlreadyExistsException(category, name);
        }
    }

    private static DictionariesEntity entityFromDictionaries(Dictionaries dictionaries) {
        return new DictionariesEntity(null, dictionaries.getCategory(), dictionaries.getName(), dictionaries.getOrder(), dictionaries.getMainValue(),
                dictionaries.getSecondaryValue());
    }

    private static DictionariesEntity entityFromValues(String category, String name, DictionariesValues values) {
        return new DictionariesEntity(null, category, name, values.getOrder(), values.getMainValue(), values.getSecondaryValue());
    }

    private static Dictionaries entityToDictionaries(DictionariesEntity entity) {
        return new Dictionaries(entity.getCategory(), entity.getName(), entity.getOrder(), entity.getMainValue(), entity.getSecondaryValue());
    }

    private static DictionariesValues entityToValues(DictionariesEntity entity) {
        return new DictionariesValues(entity.getOrder(), entity.getMainValue(), entity.getSecondaryValue());
    }
}

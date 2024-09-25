package ru.tbank.dictionaries.helidon;

import io.helidon.common.Weight;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import java.util.Optional;

@Weight(100)
public class DictionariesMapperProvider implements DbMapperProvider {

    public static final DictionariesMapper MAPPER = new DictionariesMapper();

    public static final DictionariesValuesMapper VALUES_MAPPER = new DictionariesValuesMapper();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(Dictionaries.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        } else if (type.equals(DictionariesValues.class)) {
            return Optional.of((DbMapper<T>) VALUES_MAPPER);
        }
        return Optional.empty();
    }
}

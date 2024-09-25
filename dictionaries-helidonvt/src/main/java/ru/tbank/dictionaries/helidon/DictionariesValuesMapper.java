package ru.tbank.dictionaries.helidon;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionariesValuesMapper implements DbMapper<DictionariesValues> {

    @Override
    public DictionariesValues read(DbRow row) {
        Integer order = row.column("sorder").get(Integer.class);
        String mainValue = row.column("main_value").getString();
        String secondaryValue = row.column("secondary_value").getString();
        return new DictionariesValues(order, mainValue, secondaryValue);
    }

    @Override
    public Map<String, Object> toNamedParameters(DictionariesValues value) {
        Map<String, Object> map = new HashMap<>(11);
        map.put("sorder", value.getOrder());
        map.put("main_value", value.getMainValue());
        map.put("secondary_value", value.getSecondaryValue());
        return map;
    }

    @Override
    public List<Object> toIndexedParameters(DictionariesValues value) {
        List<Object> list = new ArrayList<>(8);
        list.add(value.getOrder());
        list.add(value.getMainValue());
        list.add(value.getSecondaryValue());
        return list;
    }
}

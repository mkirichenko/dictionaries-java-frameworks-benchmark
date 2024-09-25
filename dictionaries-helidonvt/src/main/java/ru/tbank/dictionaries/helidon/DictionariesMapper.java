package ru.tbank.dictionaries.helidon;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionariesMapper implements DbMapper<Dictionaries> {

    @Override
    public Dictionaries read(DbRow row) {
        String category = row.column("category").getString();
        String name = row.column("name").getString();
        Integer order = row.column("sorder").get(Integer.class);
        String mainValue = row.column("main_value").getString();
        String secondaryValue = row.column("secondary_value").getString();
        return new Dictionaries(category, name, order, mainValue, secondaryValue);
    }

    @Override
    public Map<String, Object> toNamedParameters(Dictionaries value) {
        Map<String, Object> map = new HashMap<>(14);
        map.put("category", value.getCategory());
        map.put("name", value.getName());
        map.put("sorder", value.getOrder());
        map.put("main_value", value.getMainValue());
        map.put("secondary_value", value.getSecondaryValue());
        return map;
    }

    @Override
    public List<Object> toIndexedParameters(Dictionaries value) {
        List<Object> list = new ArrayList<>(10);
        list.add(value.getCategory());
        list.add(value.getName());
        list.add(value.getOrder());
        list.add(value.getMainValue());
        list.add(value.getSecondaryValue());
        return list;
    }
}

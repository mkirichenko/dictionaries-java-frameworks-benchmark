package ru.tbank.dictionaries.springtomcatvt;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DictionariesValuesRowMapper implements RowMapper<Dictionaries> {

    @Override
    public Dictionaries mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Dictionaries(
                rs.getString("category"),
                rs.getString("name"),
                rs.getInt("sorder"),
                rs.getString("main_value"),
                rs.getString("secondary_value"));
    }
}

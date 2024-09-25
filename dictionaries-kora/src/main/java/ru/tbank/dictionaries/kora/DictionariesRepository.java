package ru.tbank.dictionaries.kora;

import jakarta.annotation.Nullable;
import ru.tinkoff.kora.database.common.UpdateCount;
import ru.tinkoff.kora.database.common.annotation.Query;
import ru.tinkoff.kora.database.common.annotation.Repository;
import ru.tinkoff.kora.database.jdbc.JdbcRepository;

@Repository
public interface DictionariesRepository extends JdbcRepository {

    @Query("SELECT %{return#selects} FROM %{return#table} WHERE category = :category AND name = :name")
    @Nullable
    DictionariesEntity findByCategoryAndName(String category, String name);

    @Query("SELECT COUNT(1) FROM table_dictionaries WHERE category = :category AND name = :name")
    int existByCategoryAndName(String category, String name);

    @Query("INSERT INTO table_dictionaries(id, category, name, sorder, main_value, secondary_value) "
            + "VALUES ((SELECT nextval('dictionaries_id_seq')), :entity.category, :entity.name, :entity.order, "
            + ":entity.mainValue, :entity.secondaryValue)")
    void insert(DictionariesEntity entity);

    @Query("UPDATE %{entity#table} SET %{entity#updates-=id,category,name} WHERE category = :category AND name = :name")
    UpdateCount updateByCategoryAndName(String category, String name, DictionariesEntity entity);

    @Query("DELETE FROM table_dictionaries WHERE category = :category AND name = :name")
    UpdateCount deleteByCategoryAndName(String category, String name);
}

package ru.tbank.dictionaries.springtomcatvt;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface DictionariesRepository extends CrudRepository<DictionariesEntity, Long> {

    DictionariesEntity findByCategoryAndName(String category, String name);

    boolean existsByCategoryAndName(String category, String name);

    @Modifying
    @Query("DELETE FROM table_dictionaries t WHERE t.category = :category AND t.name = :name")
    boolean deleteByCategoryAndName(String category, String name);
}

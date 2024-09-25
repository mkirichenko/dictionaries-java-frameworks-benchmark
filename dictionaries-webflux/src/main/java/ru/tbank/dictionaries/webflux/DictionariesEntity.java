package ru.tbank.dictionaries.webflux;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "table_dictionaries")
public class DictionariesEntity {

    @Id
    private Long id;
    private String category;
    private String name;
    @Column("sorder")
    private Integer order;
    @Column("main_value")
    private String mainValue;
    @Column("secondary_value")
    private String secondaryValue;

    public DictionariesEntity() {
    }

    @PersistenceCreator
    public DictionariesEntity(Long id, String category, String name, Integer order, String mainValue, String secondaryValue) {

        this.id = id;
        this.category = category;
        this.name = name;
        this.order = order;
        this.mainValue = mainValue;
        this.secondaryValue = secondaryValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getMainValue() {
        return mainValue;
    }

    public void setMainValue(String mainValue) {
        this.mainValue = mainValue;
    }

    public String getSecondaryValue() {
        return secondaryValue;
    }

    public void setSecondaryValue(String secondaryValue) {
        this.secondaryValue = secondaryValue;
    }
}

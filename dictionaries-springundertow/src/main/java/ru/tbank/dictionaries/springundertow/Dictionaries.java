package ru.tbank.dictionaries.springundertow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Dictionaries {

    @NotBlank
    private String category;

    @Pattern(regexp = "^[^/#?]+$")
    @NotBlank
    private String name;

    private Integer order;
    private String mainValue;
    private String secondaryValue;

    public Dictionaries() {
    }

    public Dictionaries(String category, String name, Integer order, String mainValue, String secondaryValue) {

        this.category = category;
        this.name = name;
        this.order = order;
        this.mainValue = mainValue;
        this.secondaryValue = secondaryValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

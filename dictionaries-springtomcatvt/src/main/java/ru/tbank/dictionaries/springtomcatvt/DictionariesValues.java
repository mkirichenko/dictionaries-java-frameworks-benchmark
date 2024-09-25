package ru.tbank.dictionaries.springtomcatvt;

public class DictionariesValues {

    private Integer order;
    private String mainValue;
    private String secondaryValue;

    public DictionariesValues() {
    }

    public DictionariesValues(Integer order, String mainValue, String secondaryValue) {

        this.order = order;
        this.mainValue = mainValue;
        this.secondaryValue = secondaryValue;
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

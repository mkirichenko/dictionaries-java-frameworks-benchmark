package ru.tbank.dictionaries.kora;

import jakarta.annotation.Nullable;
import ru.tinkoff.kora.json.common.annotation.Json;

@Json
public class DictionariesValues {

    @Nullable
    private Integer order;
    @Nullable
    private String mainValue;
    @Nullable
    private String secondaryValue;

    public DictionariesValues() {
    }

    public DictionariesValues(@Nullable Integer order, @Nullable String mainValue, @Nullable String secondaryValue) {

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

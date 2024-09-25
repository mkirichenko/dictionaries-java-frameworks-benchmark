package ru.tbank.dictionaries.kora;

import jakarta.annotation.Nullable;
import ru.tinkoff.kora.json.common.annotation.Json;
import ru.tinkoff.kora.validation.common.annotation.NotBlank;
import ru.tinkoff.kora.validation.common.annotation.Pattern;
import ru.tinkoff.kora.validation.common.annotation.Valid;

@Valid
@Json
public class Dictionaries {

    @NotBlank
    private String category;

    @Pattern("^[^/#?]+$")
    private String name;

    @Nullable
    private Integer order;
    @Nullable
    private String mainValue;
    @Nullable
    private String secondaryValue;

    public Dictionaries() {
    }

    public Dictionaries(String category, String name,
                        @Nullable Integer order, @Nullable String mainValue, @Nullable String secondaryValue) {

        this.category = category;
        this.name = name;
        this.order = order;
        this.mainValue = mainValue;
        this.secondaryValue = secondaryValue;
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

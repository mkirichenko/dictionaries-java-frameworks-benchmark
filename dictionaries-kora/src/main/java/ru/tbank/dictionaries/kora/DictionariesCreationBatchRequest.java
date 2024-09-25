package ru.tbank.dictionaries.kora;

import java.util.List;
import ru.tinkoff.kora.json.common.annotation.Json;
import ru.tinkoff.kora.validation.common.annotation.NotEmpty;
import ru.tinkoff.kora.validation.common.annotation.Valid;

@Valid
@Json
public final class DictionariesCreationBatchRequest {

    @Valid
    @NotEmpty
    private List<Dictionaries> requests;

    public DictionariesCreationBatchRequest() {
    }

    public DictionariesCreationBatchRequest(List<Dictionaries> requests) {
        this.requests = requests;
    }

    public List<Dictionaries> getRequests() {
        return requests;
    }

    public void setRequests(List<Dictionaries> requests) {
        this.requests = requests;
    }
}

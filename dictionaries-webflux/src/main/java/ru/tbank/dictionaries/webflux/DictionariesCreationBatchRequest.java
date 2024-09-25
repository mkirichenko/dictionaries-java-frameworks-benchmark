package ru.tbank.dictionaries.webflux;

import jakarta.validation.Valid;
import java.util.List;

public final class DictionariesCreationBatchRequest {

    @Valid
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

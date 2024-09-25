package ru.tbank.dictionaries.helidon;

import java.util.List;

public final class DictionariesCreationBatchRequest {

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

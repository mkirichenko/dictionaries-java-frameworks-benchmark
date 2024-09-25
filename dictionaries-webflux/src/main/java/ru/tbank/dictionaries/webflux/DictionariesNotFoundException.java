package ru.tbank.dictionaries.webflux;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DictionariesNotFoundException extends ResponseStatusException {

    public DictionariesNotFoundException(String category, String name) {
        super(HttpStatus.NOT_FOUND, "Dictionary " + category + "/" + name + " not found");
    }
}

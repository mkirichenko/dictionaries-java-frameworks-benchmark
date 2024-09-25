package ru.tbank.dictionaries.springundertow;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DictionariesAlreadyExistsException extends ResponseStatusException {

    public DictionariesAlreadyExistsException(String category, String name) {
        super(HttpStatus.CONFLICT, "Dictionary with category: " + category + " and name: " + name + " already exists");
    }
}

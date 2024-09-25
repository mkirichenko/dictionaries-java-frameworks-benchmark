package ru.tbank.dictionaries.helidon;

import io.helidon.http.HttpException;
import io.helidon.http.Status;

public class DictionariesAlreadyExistsException extends HttpException {

    public DictionariesAlreadyExistsException(String category, String name) {
        super(getMessage(category, name), Status.CONFLICT_409);
    }

    private static String getMessage(String category, String name) {
        return "dictionaries " + category + "/" + name + " already exists";
    }
}

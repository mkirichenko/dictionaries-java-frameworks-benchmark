package ru.tbank.dictionaries.helidon;

import io.helidon.http.HttpException;
import io.helidon.http.Status;

public class DictionariesNotFoundException extends HttpException {

    public DictionariesNotFoundException(String category, String name) {
        super(getMessage(category, name), Status.NOT_FOUND_404);
    }

    private static String getMessage(String category, String name) {
        return "dictionaries " + category + "/" + name + " not found";
    }
}

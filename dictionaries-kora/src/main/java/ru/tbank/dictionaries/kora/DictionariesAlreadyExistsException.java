package ru.tbank.dictionaries.kora;

import java.nio.charset.StandardCharsets;
import ru.tinkoff.kora.http.common.header.HttpHeaders;
import ru.tinkoff.kora.http.server.common.HttpServerResponseException;

public class DictionariesAlreadyExistsException extends HttpServerResponseException {

    public DictionariesAlreadyExistsException(String category, String name) {
        super(null, getMessage(category, name), 409,
                "application/json;charset=utf-8", StandardCharsets.UTF_8.encode(getBody(category, name)), HttpHeaders.of());
    }

    private static String getMessage(String category, String name) {
        return "dictionaries " + category + "/" + name + " already exists";
    }

    private static String getBody(String category, String name) {
        return "{" +
                "\"code\":\"error.409\"," +
                "\"message\":\"" + getMessage(category, name) + "\"" +
                "}";
    }
}

package ru.tbank.dictionaries.helidon;

import java.util.List;
import java.util.regex.Pattern;

public class DictionariesValidator {

    private static final Pattern DICTIONARIES_NAME_PATTERN = Pattern.compile("^[^/#?]+$");

    public static boolean validate(Dictionaries dictionaries) {
        if (dictionaries == null) {
            return false;
        }
        if (dictionaries.getCategory() == null || dictionaries.getCategory().isBlank()) {
            return false;
        }
        if (dictionaries.getName() == null || dictionaries.getName().isEmpty()) {
            return false;
        }
        if (!DICTIONARIES_NAME_PATTERN.matcher(dictionaries.getName()).matches()) {
            return false;
        }
        return true;
    }

    public static boolean validate(DictionariesCreationBatchRequest batchRequest) {
        if (batchRequest == null) {
            return false;
        }
        List<Dictionaries> requests = batchRequest.getRequests();
        if (requests == null) {
            return false;
        }
        for (Dictionaries request : requests) {
            if (!validate(request)) {
                return false;
            }
        }
        return true;
    }
}

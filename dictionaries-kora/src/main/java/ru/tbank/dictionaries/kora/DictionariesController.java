package ru.tbank.dictionaries.kora;

import jakarta.annotation.Nullable;
import java.util.List;
import ru.tinkoff.kora.http.common.HttpMethod;
import ru.tinkoff.kora.http.common.annotation.HttpRoute;
import ru.tinkoff.kora.http.common.annotation.InterceptWith;
import ru.tinkoff.kora.http.common.annotation.Path;
import ru.tinkoff.kora.http.common.annotation.Query;
import ru.tinkoff.kora.http.server.common.annotation.HttpController;
import ru.tinkoff.kora.json.common.annotation.Json;
import ru.tinkoff.kora.validation.common.Validator;

@InterceptWith(JwtVerifierHttpServerInterceptor.class)
@HttpController("/api/v1/dictionary")
public final class DictionariesController {

    private final DictionariesService service;
    private final Validator<Dictionaries> dictionariesValidator;
    private final Validator<DictionariesCreationBatchRequest> dictionariesBatchValidator;

    public DictionariesController(DictionariesService service, Validator<Dictionaries> dictionariesValidator,
                                  Validator<DictionariesCreationBatchRequest> dictionariesBatchValidator) {

        this.service = service;
        this.dictionariesValidator = dictionariesValidator;
        this.dictionariesBatchValidator = dictionariesBatchValidator;
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "")
    public List<Dictionaries> getAll(
            @Nullable @Query String category,
            @Nullable @Query String name,
            @Nullable @Query String mainValue,
            @Nullable @Query String secondaryValue) {

        return service.getAll(category, name, mainValue, secondaryValue);
    }

    @Json
    @HttpRoute(method = HttpMethod.GET, path = "{category}/{name}")
    public Dictionaries get(@Path String category, @Path String name) {
        return service.get(category, name);
    }

    @HttpRoute(method = HttpMethod.POST, path = "")
    public void create(@Json Dictionaries request) {
        dictionariesValidator.validateAndThrow(request);
        service.create(request);
    }

    @HttpRoute(method = HttpMethod.POST, path = "batch")
    public void createFromList(@Json DictionariesCreationBatchRequest request) {
        dictionariesBatchValidator.validateAndThrow(request);
        service.createFromList(request.getRequests());
    }

    @HttpRoute(method = HttpMethod.PUT, path = "{category}/{name}")
    public void update(@Path String category, @Path String name, @Json DictionariesValues request) {
        service.update(category, name, request);
    }

    @HttpRoute(method = HttpMethod.PUT, path = "")
    public void updateWithQueryParameters(@Query String category, @Query String name, @Json DictionariesValues request) {
        service.update(category, name, request);
    }

    @HttpRoute(method = HttpMethod.DELETE, path = "{category}/{name}")
    public void delete(@Path String category, @Path String name) {
        service.delete(category, name);
    }

    @HttpRoute(method = HttpMethod.DELETE, path = "")
    public void deleteWithQueryParameters(@Query String category, @Query String name) {
        service.delete(category, name);
    }
}

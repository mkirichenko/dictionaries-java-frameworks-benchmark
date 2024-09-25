package ru.tbank.dictionaries.webflux;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/v1/dictionary")
public class DictionariesResource {

    private final DictionariesService dictionariesService;

    public DictionariesResource(DictionariesService dictionariesService) {
        this.dictionariesService = dictionariesService;
    }

    @GetMapping
    public Mono<List<Dictionaries>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mainValue,
            @RequestParam(required = false) String secondaryValue) {

        return dictionariesService.getAll(category, name, mainValue, secondaryValue);
    }

    @GetMapping("/{category}/{name:.+}")
    public Mono<DictionariesValues> get(@PathVariable String category, @PathVariable String name) {
        return dictionariesService.get(category, name);
    }

    @PostMapping
    public Mono<Void> create(@Valid @RequestBody Dictionaries request) {
        return dictionariesService.create(request);
    }

    @PostMapping("batch")
    public Mono<Void> createFromList(@Valid @RequestBody DictionariesCreationBatchRequest batchRequest) {
        return dictionariesService.createFromList(batchRequest.getRequests());
    }

    @PutMapping("/{category}/{name:.+}")
    public Mono<Void> update(@PathVariable String category, @PathVariable String name, @RequestBody DictionariesValues request) {
        return dictionariesService.update(category, name, request);
    }

    @PutMapping
    public Mono<Void> updateWithQueryParameters(String category, String name, @RequestBody DictionariesValues request) {
        return dictionariesService.update(category, name, request);
    }

    @DeleteMapping("/{category}/{name:.+}")
    public Mono<Void> delete(@PathVariable String category, @PathVariable String name) {
        return dictionariesService.delete(category, name);
    }

    @DeleteMapping
    public Mono<Void> deleteWithQueryParameters(String category, String name) {
        return dictionariesService.delete(category, name);
    }
}

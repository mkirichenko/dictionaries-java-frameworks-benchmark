package ru.tbank.dictionaries.springtomcatvt;

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

@RestController
@RequestMapping(path = "/api/v1/dictionary")
public class DictionariesResource {

    private final DictionariesService dictionariesService;

    public DictionariesResource(DictionariesService dictionariesService) {
        this.dictionariesService = dictionariesService;
    }

    @GetMapping
    public List<Dictionaries> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mainValue,
            @RequestParam(required = false) String secondaryValue) {

        return dictionariesService.getAll(category, name, mainValue, secondaryValue);
    }

    @GetMapping("/{category}/{name:.+}")
    public DictionariesValues get(@PathVariable String category, @PathVariable String name) {
        return dictionariesService.get(category, name);
    }

    @PostMapping
    public void create(@Valid @RequestBody Dictionaries request) {
        dictionariesService.create(request);
    }

    @PostMapping("batch")
    public void createFromList(@Valid @RequestBody DictionariesCreationBatchRequest batchRequest) {
        dictionariesService.createFromList(batchRequest.getRequests());
    }

    @PutMapping("/{category}/{name:.+}")
    public void update(@PathVariable String category, @PathVariable String name, @RequestBody DictionariesValues request) {
        dictionariesService.update(category, name, request);
    }

    @PutMapping
    public void updateWithQueryParameters(String category, String name, @RequestBody DictionariesValues request) {
        dictionariesService.update(category, name, request);
    }

    @DeleteMapping("/{category}/{name:.+}")
    public void delete(@PathVariable String category, @PathVariable String name) {
        dictionariesService.delete(category, name);
    }

    @DeleteMapping
    public void deleteWithQueryParameters(String category, String name) {
        dictionariesService.delete(category, name);
    }
}

package ru.tbank.dictionaries.ktor.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<Dictionaries> { dictionaries ->
            validateDictionaries(dictionaries)
        }

        validate<DictionariesBatchRequest> { dictionariesBatchRequest ->
            for (dictionaries in dictionariesBatchRequest.requests) {
                val result = validateDictionaries(dictionaries)
                if (result is ValidationResult.Invalid) {
                    return@validate result
                }
            }
            ValidationResult.Valid
        }
    }

    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest, mapOf(
                    "code" to "error.400",
                    "message" to cause.reasons.joinToString()
                )
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError, mapOf(
                    "code" to "error.500",
                    "message" to cause.message
                )
            )
        }
    }
}

private fun validateDictionaries(dictionaries: Dictionaries): ValidationResult {
    if (dictionaries.category.isNullOrBlank()) {
        return ValidationResult.Invalid("category must not be blank")
    }
    if (dictionaries.name.isNullOrBlank()) {
        return ValidationResult.Invalid("name must not be blank")
    }
    if (dictionaries.name.contains('/') || dictionaries.name.contains('?') || dictionaries.name.contains('#')) {
        return ValidationResult.Invalid("name must not contain '/', '?', '#' symbols")
    }
    return ValidationResult.Valid
}

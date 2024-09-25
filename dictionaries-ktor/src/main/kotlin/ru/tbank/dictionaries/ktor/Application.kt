package ru.tbank.dictionaries.ktor

import io.ktor.server.application.*
import ru.tbank.dictionaries.ktor.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureMonitoring()
    configureRequestValidation()
    configureDictionariesRouting()
}

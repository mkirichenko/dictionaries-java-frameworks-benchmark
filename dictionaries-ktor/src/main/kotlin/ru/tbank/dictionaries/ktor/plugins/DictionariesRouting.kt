package ru.tbank.dictionaries.ktor.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.Schema

fun Application.configureDictionariesRouting() {
    val database = connectToPostgresViaExposed()
    val dictionariesService = DictionariesService(database)

    routing {
        // Get all
        get("/api/v1/dictionary") {
            val category = call.request.queryParameters["category"]
            val name = call.request.queryParameters["name"]
            val mainValue = call.request.queryParameters["mainValue"]
            val secondaryValue = call.request.queryParameters["secondaryValue"]

            call.respond(
                HttpStatusCode.OK, dictionariesService.getAll(
                    category,
                    name,
                    mainValue,
                    secondaryValue
                )
            )
        }

        // Get
        get("/api/v1/dictionary/{category}/{name}") {
            val category = call.parameters["category"]!!
            val name = call.parameters["name"]!!
            val response = dictionariesService.get(category, name)

            if (response != null) {
                call.respond(HttpStatusCode.OK, response)
            } else {
                call.respond(HttpStatusCode.NotFound, "dictionary ${category}/${name} not found")
            }
        }

        // Create
        post("/api/v1/dictionary") {
            val request = call.receive<Dictionaries>()
            dictionariesService.create(request)
            call.respond(HttpStatusCode.Created)
        }

        // Batch create
        post("/api/v1/dictionary/batch") {
            val request = call.receive<DictionariesBatchRequest>()
            dictionariesService.batchCreate(request.requests)
            call.respond(HttpStatusCode.Created)
        }

        // Update via query parameters
        put("/api/v1/dictionary") {
            val category = call.request.queryParameters["category"]!!
            val name = call.request.queryParameters["name"]!!

            val request = call.receive<Dictionaries.Values>()
            dictionariesService.update(category, name, request)
            call.respond(HttpStatusCode.OK)
        }

        // Update
        put("/api/v1/dictionary/{category}/{name}") {
            val category = call.parameters["category"]!!
            val name = call.parameters["name"]!!

            val request = call.receive<Dictionaries.Values>()
            dictionariesService.update(category, name, request)
            call.respond(HttpStatusCode.OK)
        }

        // Delete via query parameters
        delete("/api/v1/dictionary") {
            val category = call.request.queryParameters["category"]!!
            val name = call.request.queryParameters["name"]!!

            dictionariesService.delete(category, name)
            call.respond(HttpStatusCode.OK)
        }

        // Delete
        delete("/api/v1/dictionary/{category}/{name}") {
            val category = call.parameters["category"]!!
            val name = call.parameters["name"]!!

            dictionariesService.delete(category, name)
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Application.connectToPostgresViaExposed(): Database {
    Class.forName("org.postgresql.Driver")

    val url = environment.config.property("postgres.url").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()
    val schema = environment.config.property("postgres.schema").getString()

    return Database.connect(
        url = url,
        user = user,
        driver = "org.postgresql.Driver",
        password = password,
        databaseConfig = DatabaseConfig {
            defaultSchema = Schema(
                name = schema
            )
        }
    )
}

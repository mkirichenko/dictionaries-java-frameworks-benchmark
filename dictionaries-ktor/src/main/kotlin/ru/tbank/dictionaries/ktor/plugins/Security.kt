package ru.tbank.dictionaries.ktor.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    val rsaPublicKeyString = environment.config.property("jwt.public-key").getString()
        .replace("\\n", "\n")
    val rsaPublicKey = convertStringToRsaPublicKey(rsaPublicKeyString)

    authentication {
        jwt {
            verifier(
                JWT
                    .require(Algorithm.RSA256(rsaPublicKey))
                    .build()
            )

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized, mapOf(
                        "code" to "error.401",
                        "message" to "token is not present or expired"
                    )
                )
            }
        }
    }
}

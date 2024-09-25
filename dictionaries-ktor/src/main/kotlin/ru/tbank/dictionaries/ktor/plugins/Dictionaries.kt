package ru.tbank.dictionaries.ktor.plugins

import kotlinx.serialization.Serializable

@Serializable
data class Dictionaries(
    val category: String?,
    val name: String?,
    val order: Int? = null,
    val mainValue: String? = null,
    val secondaryValue: String? = null,
) {
    @Serializable
    data class Values(
        val order: Int? = null,
        val mainValue: String? = null,
        val secondaryValue: String? = null,
    )
}

package ru.tbank.dictionaries.ktor.plugins

import kotlinx.serialization.Serializable

@Serializable
data class DictionariesBatchRequest(val requests: List<Dictionaries>)

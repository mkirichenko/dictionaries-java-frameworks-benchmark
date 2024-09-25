package ru.tbank.dictionaries.ktor.plugins

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DictionariesService(database: Database) {

    object DictionariesEntity : Table(name = "table_dictionaries") {
        val id = integer("id").autoIncrement(idSeqName = "dictionaries_id_seq")
        val category = varchar(name = "category", length = 255).nullable()
            .index(customIndexName = "table_dictionaries_category")
        val name = varchar(name = "name", length = 255).nullable()
        val order = integer(name = "sorder").nullable()
        val mainValue = varchar(name = "main_value", length = 512).nullable()
        val secondaryValue = varchar(name = "secondary_value", length = 512).nullable()

        override val primaryKey = PrimaryKey(id, name = "pk_table_dictionaries")

        init {
            uniqueIndex(customIndexName = "name_category_unique", category, name)
        }
    }

    init {
        transaction(database) {
            SchemaUtils.create(DictionariesEntity)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun getAll(
        category: String?,
        name: String?,
        mainValue: String?,
        secondaryValue: String?
    ): List<Dictionaries> {

        val conditions: MutableList<Op<Boolean>> = mutableListOf()
        if (category != null) {
            conditions.add(DictionariesEntity.category eq category)
        }
        if (name != null) {
            conditions.add(DictionariesEntity.name eq name)
        }
        if (mainValue != null) {
            conditions.add(DictionariesEntity.mainValue eq mainValue)
        }
        if (secondaryValue != null) {
            conditions.add(DictionariesEntity.secondaryValue eq secondaryValue)
        }

        val dictionariesQuery = if (conditions.isEmpty()) {
            DictionariesEntity.selectAll()
        } else {
            DictionariesEntity.select(conditions.compoundAnd())
        }

        return dbQuery {
            dictionariesQuery
                .orderBy(DictionariesEntity.category)
                .orderBy(DictionariesEntity.order)
                .map {
                    Dictionaries(
                        it[DictionariesEntity.category],
                        it[DictionariesEntity.name],
                        it[DictionariesEntity.order],
                        it[DictionariesEntity.mainValue],
                        it[DictionariesEntity.secondaryValue]
                    )
                }
        }
    }

    suspend fun get(category: String, name: String): Dictionaries? {
        return dbQuery {
            DictionariesEntity.select { (DictionariesEntity.category eq category) and (DictionariesEntity.name eq name) }
                .map {
                    Dictionaries(
                        it[DictionariesEntity.category],
                        it[DictionariesEntity.name],
                        it[DictionariesEntity.order],
                        it[DictionariesEntity.mainValue],
                        it[DictionariesEntity.secondaryValue]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun create(dictionariesRequest: Dictionaries) {
        dbQuery {
            checkIfSettingExists(dictionariesRequest)
            DictionariesEntity.insert {
                it[category] = dictionariesRequest.category
                it[name] = dictionariesRequest.name
                it[order] = dictionariesRequest.order
                it[mainValue] = dictionariesRequest.mainValue
                it[secondaryValue] = dictionariesRequest.secondaryValue
            }
        }
    }

    suspend fun batchCreate(list: List<Dictionaries>) {
        dbQuery {
            for (dictionariesRequest in list) {
                checkIfSettingExists(dictionariesRequest)
                DictionariesEntity.insert {
                    it[category] = dictionariesRequest.category
                    it[name] = dictionariesRequest.name
                    it[order] = dictionariesRequest.order
                    it[mainValue] = dictionariesRequest.mainValue
                    it[secondaryValue] = dictionariesRequest.secondaryValue
                }
            }
        }
    }

    private fun checkIfSettingExists(dictionariesRequest: Dictionaries) {
        val dictionaryExists = DictionariesEntity
            .select {
                (DictionariesEntity.category eq dictionariesRequest.category) and (DictionariesEntity.name eq dictionariesRequest.name)
            }
            .any()
        if (dictionaryExists) {
            throw Exception("dictionary ${dictionariesRequest.category}/${dictionariesRequest.name} already exists")
        }
    }

    suspend fun update(category: String, name: String, dictionariesRequest: Dictionaries.Values) {
        dbQuery {
            DictionariesEntity.update({ (DictionariesEntity.category eq category) and (DictionariesEntity.name eq name) }) {
                it[order] = dictionariesRequest.order
                it[mainValue] = dictionariesRequest.mainValue
                it[secondaryValue] = dictionariesRequest.secondaryValue
            }
        }
    }

    suspend fun delete(category: String, name: String) {
        dbQuery {
            DictionariesEntity.deleteWhere { (DictionariesEntity.category eq category) and (DictionariesEntity.name eq name) }
        }
    }
}

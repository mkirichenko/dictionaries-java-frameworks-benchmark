package ru.tbank.dictionaries.kora;

import jakarta.annotation.Nullable;
import ru.tinkoff.kora.database.common.annotation.Column;
import ru.tinkoff.kora.database.common.annotation.Id;
import ru.tinkoff.kora.database.common.annotation.Table;

@Table("table_dictionaries")
public record DictionariesEntity(
        @Id long id,
        @Nullable String category,
        @Nullable String name,
        @Nullable @Column("sorder") Integer order,
        @Nullable String mainValue,
        @Nullable String secondaryValue
) {
}

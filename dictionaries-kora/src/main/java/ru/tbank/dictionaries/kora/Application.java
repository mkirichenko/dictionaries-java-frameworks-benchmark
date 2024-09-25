package ru.tbank.dictionaries.kora;

import ru.tinkoff.kora.application.graph.KoraApplication;
import ru.tinkoff.kora.common.KoraApp;
import ru.tinkoff.kora.config.yaml.YamlConfigModule;
import ru.tinkoff.kora.database.jdbc.JdbcDatabaseModule;
import ru.tinkoff.kora.http.server.undertow.UndertowHttpServerModule;
import ru.tinkoff.kora.json.module.JsonModule;
import ru.tinkoff.kora.logging.logback.LogbackModule;
import ru.tinkoff.kora.micrometer.module.MetricsModule;
import ru.tinkoff.kora.validation.module.ValidationModule;

@KoraApp
public interface Application extends YamlConfigModule, UndertowHttpServerModule, JsonModule, JdbcDatabaseModule,
        ValidationModule, LogbackModule, MetricsModule {

    static void main(String[] args) {
        KoraApplication.run(ApplicationGraph::graph);
    }
}

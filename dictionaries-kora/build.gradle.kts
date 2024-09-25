plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

group = "ru.tbank.dictionaries.kora"
version = "0.1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainClass.set("ru.tbank.dictionaries.kora.Application")
}

configurations {
    val koraBom by creating
    implementation.get().extendsFrom(koraBom)
    annotationProcessor.get().extendsFrom(koraBom)
}

dependencies {
    val koraBom: Configuration by configurations.getting
    koraBom(platform("ru.tinkoff.kora:kora-parent:1.1.7"))

    annotationProcessor("ru.tinkoff.kora:annotation-processors")
    annotationProcessor("org.slf4j:slf4j-simple:2.0.7")

    implementation("ru.tinkoff.kora:http-server-undertow")
    implementation("ru.tinkoff.kora:json-module")
    implementation("ru.tinkoff.kora:config-yaml")
    implementation("ru.tinkoff.kora:logging-logback")

    implementation("ru.tinkoff.kora:database-jdbc")
    implementation("org.postgresql:postgresql:42.5.1")

    implementation("ru.tinkoff.kora:validation-module")

    implementation("ru.tinkoff.kora:micrometer-module")

    implementation("com.auth0:java-jwt:4.4.0")
}

tasks {
    shadowJar {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClass))
        }
    }
}

### Сравнение разных фреймворков на примере простого сервиса справочников

#### Фреймворки
- Helidon VT — `/dictionaries-helidonvt`
- Kora — `/dictionaries-kora`
- Ktor — `/dictionaries-ktor`
- Spring Boot Undertow — `/dictionaries-springundertow`
- Spring Boot Tomcat VT — `/dictionaries-springtomcatvt`
- Spring Boot WebFlux — `/dictionaries-webflux`

#### Какждое приложение содержит
- CRUD
- Валидацию
- Авторизацию
- Метрики

#### Требования для сборки и запуска
- JDK 21
- _(Только для Ktor)_ Kotlin 2
- PostgreSQL 16 
- [k6](https://k6.io)

#### Подготовка базы данных
1. Создать в базе данных необходимые сущности, схема описана в файле `database-schema.sql`
2. Заполнить их случайными данными любым удобным способом

#### Порядок действий для сборки
1. Добавить публичный RSA ключ для валидации авторизационного токена во все сервисы
   1. Здесь и далее замену нужно производить во всех файлах
   2. Для этого сдедует все вхождения строки `EXPECTED_RSA_PUBLIC_KEY_X509_HERE` заменить на содержимое ключа в формате PEM X509
   3. _(Только для Helidon)_ Для этого сдедует все вхождения строки `EXPECTED_RSA_PUBLIC_KEY_JWK_HERE` заменить на содержимое ключа в формате JWK
2. _(Только для Helidon)_ Указать audience для валидации авторизационного JWT-токена
   1. Для этого сдедует все вхождения строки `EXPECTED_HELIDON_JWT_VERIFY_AUD_HERE` заменить на содержимое ключа в формате PEM PKCS 8
3. Добавить данные для подключения к базе данных во все сервисы
   1. Для этого сдедует все вхождения строки `EXPECTED_POSTGRES_JDBC_URL_HERE` заменить на jdbc-адрес подключения к БД
   2. _(только для Spring Boot WebFlux)_ Все вхождения строки `EXPECTED_POSTGRES_R2DBC_URL_HERE` заменить на r2dbc-адрес подключения к БД
   3. Все вхождения строки `EXPECTED_POSTGRES_DATABASE_USERNAME_HERE` заменить на логин для подключения к БД
   4. Все вхождения строки `EXPECTED_POSTGRES_DATABASE_PASSWORD_HERE` заменить на пароль для подключения к БД
   5. Все вхождения строки `EXPECTED_POSTGRES_DATABASE_SCHEMA_HERE` заменить на название схемы БД, которую следует использовать
3. Добавить JWT-токен для выполнения запросов в скрипт нагрузочного теста
   1. Для этого сдедует все вхождения строки `EXPECTED_JWT_TOKEN_IN_AUTH_HEADER_HERE` заменить на содержимое токена
4. Добавить список категорий справочников в скрипт нагрузочного теста
   1. Для этого сдедует все вхождения строки `EXPECTED_DICTIONARIES_CATEGORIES_JSON_LIST_CONTENT_HERE` заменить на содержимое список категорий, в одиночных кавычках, через запятую
5. Собрать все сервисы
   1. Для сборки достаточно выполнить `./mvnw verify` или `./gradlew build` в директории каждого сервиса

#### Порядок действий для запуска
1. Убедиться, что база данных доступна и содержит данные для теста
2. Запустить бенчмарк `./run-benchmark-on-k6.sh`
	1. Отчеты будут доступны в директории `reports`
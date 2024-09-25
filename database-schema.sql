CREATE TABLE IF NOT EXISTS table_dictionaries(
  id BIGINT NOT NULL
  category VARCHAR(255),
  name VARCHAR(255),
  sorder INTEGER,
  main_value VARCHAR(512),
  secondary_value VARCHAR(512),

  CONSTRAINT pk_table_dictionaries PRIMARY KEY  (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS name_category_unique ON table_dictionaries(category, name);

CREATE INDEX IF NOT EXISTS table_dictionaries_category ON table_dictionaries(category);

CREATE SEQUENCE IF NOT EXISTS dictionaries_id_seq;

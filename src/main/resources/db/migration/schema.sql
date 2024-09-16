create table if not exists users
(
    id
    serial
    primary
    key,
    name
    varchar
(
    255
) not null );

CREATE TABLE IF NOT EXISTS url_mappings
(
    id
    VARCHAR
(
    255
) PRIMARY KEY,
    original_url TEXT NOT NULL,
    short_url TEXT NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP
    );


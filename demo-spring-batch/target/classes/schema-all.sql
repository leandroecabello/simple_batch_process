DROP TABLE person IF EXISTS;

CREATE TABLE person(
    id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(20),
    lastname VARCHAR(20),
    phone VARCHAR(10)
);
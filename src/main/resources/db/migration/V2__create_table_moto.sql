CREATE TABLE moto(
                     id IDENTITY PRIMARY KEY,
                     placa VARCHAR(10) NOT NULL UNIQUE,
                     modelo VARCHAR(100) NOT NULL,
                     status VARCHAR(20),
                     patio_id BIGINT,
                     CONSTRAINT fk_moto_patio FOREIGN KEY (patio_id) REFERENCES patio(id)
);

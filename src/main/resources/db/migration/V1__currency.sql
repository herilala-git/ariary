CREATE TABLE currency (
    code  VARCHAR2(3)  PRIMARY KEY,
    name  VARCHAR2(50) NOT NULL
);

INSERT INTO currency (code, name) VALUES ('CAD', 'Canadian dollar');
INSERT INTO currency (code, name) VALUES ('USD', 'US dollar');
INSERT INTO currency (code, name) VALUES ('EUR', 'Euro');
INSERT INTO currency (code, name) VALUES ('GBP', 'Pound sterling');
INSERT INTO currency (code, name) VALUES ('MGA', 'Malagasy ariary');

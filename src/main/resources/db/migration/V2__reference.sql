CREATE TABLE legal_entity (
    code                VARCHAR2(20)  PRIMARY KEY,
    name                VARCHAR2(100) NOT NULL,
    reporting_currency  VARCHAR2(3)   NOT NULL REFERENCES currency (code)
);

CREATE TABLE portfolio (
    code         VARCHAR2(20)  PRIMARY KEY,
    name         VARCHAR2(100) NOT NULL,
    entity_code  VARCHAR2(20)  NOT NULL REFERENCES legal_entity (code),
    parent_code  VARCHAR2(20)  REFERENCES portfolio (code)
);

CREATE TABLE counterparty (
    code    VARCHAR2(20)  PRIMARY KEY,
    name    VARCHAR2(100) NOT NULL,
    rating  VARCHAR2(5)   NOT NULL
);

CREATE TABLE calendar (
    code  VARCHAR2(20)  PRIMARY KEY,
    name  VARCHAR2(100) NOT NULL
);

CREATE TABLE holiday (
    calendar_code  VARCHAR2(20)  NOT NULL REFERENCES calendar (code),
    holiday_date   DATE          NOT NULL,
    name           VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_holiday PRIMARY KEY (calendar_code, holiday_date)
);

CREATE TABLE business_date (
    id             CHAR(1)      DEFAULT 'Y' PRIMARY KEY CHECK (id = 'Y'),
    current_day    DATE         NOT NULL,
    calendar_code  VARCHAR2(20) NOT NULL REFERENCES calendar (code)
);

INSERT INTO legal_entity VALUES ('ARIARY-CA', 'Ariary Canada', 'CAD');
INSERT INTO legal_entity VALUES ('ARIARY-US', 'Ariary United States', 'USD');

INSERT INTO portfolio VALUES ('PF-TREASURY', 'Group treasury', 'ARIARY-CA', NULL);
INSERT INTO portfolio VALUES ('PF-CASH', 'Short-term deposits', 'ARIARY-CA', 'PF-TREASURY');
INSERT INTO portfolio VALUES ('PF-FX', 'FX hedging', 'ARIARY-CA', 'PF-TREASURY');
INSERT INTO portfolio VALUES ('PF-BONDS', 'Bonds', 'ARIARY-US', 'PF-TREASURY');

INSERT INTO counterparty VALUES ('CP-BOC', 'Bank of Canada', 'AAA');
INSERT INTO counterparty VALUES ('CP-BANKA', 'Bank A', 'A+');
INSERT INTO counterparty VALUES ('CP-BANKB', 'Bank B', 'BBB');

INSERT INTO calendar VALUES ('TORONTO', 'Toronto market');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-01-01', 'New Year''s Day');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-02-15', 'Family Day');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-03-26', 'Good Friday');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-05-24', 'Victoria Day');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-07-01', 'Canada Day');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-08-02', 'Civic Holiday');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-09-06', 'Labour Day');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-10-11', 'Thanksgiving');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-12-27', 'Christmas (observed)');
INSERT INTO holiday VALUES ('TORONTO', DATE '2027-12-28', 'Boxing Day (observed)');

INSERT INTO business_date (current_day, calendar_code) VALUES (DATE '2027-09-03', 'TORONTO');

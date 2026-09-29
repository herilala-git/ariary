CREATE TABLE fx_rate (
    rate_date       DATE          NOT NULL,
    base_currency   VARCHAR2(3)   NOT NULL REFERENCES currency (code),
    quote_currency  VARCHAR2(3)   NOT NULL REFERENCES currency (code),
    rate            NUMBER(20,10) NOT NULL,
    CONSTRAINT pk_fx_rate PRIMARY KEY (rate_date, base_currency, quote_currency)
);

CREATE TABLE curve_point (
    curve_date  DATE          NOT NULL,
    currency    VARCHAR2(3)   NOT NULL REFERENCES currency (code),
    days        NUMBER(5)     NOT NULL,
    zero_rate   NUMBER(12,8)  NOT NULL,
    CONSTRAINT pk_curve_point PRIMARY KEY (curve_date, currency, days)
);

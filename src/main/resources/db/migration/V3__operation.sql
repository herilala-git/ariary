-- nocache : pas de trous dans les ids
CREATE SEQUENCE operation_seq START WITH 1000 INCREMENT BY 1 NOCACHE;

CREATE TABLE operation (
    id NUMBER(19) PRIMARY KEY,
    instrument_type VARCHAR2(20) NOT NULL,
    status VARCHAR2(12) NOT NULL,
    portfolio_code VARCHAR2(20) NOT NULL REFERENCES portfolio (code),
    counterparty_code VARCHAR2(20) NOT NULL REFERENCES counterparty (code),
    currency VARCHAR2(3) NOT NULL REFERENCES currency (code),
    notional NUMBER(20,2) NOT NULL,
    rate NUMBER(12,8),
    day_count VARCHAR2(12),
    trade_date DATE NOT NULL,
    start_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    version NUMBER(19) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX ix_operation_status ON operation (status, portfolio_code);

ALTER TABLE operation ADD (
    quote_currency    VARCHAR2(3)    REFERENCES currency (code),
    side              VARCHAR2(4),
    forward_rate      NUMBER(20,10),
    coupon_frequency  NUMBER(2)
);

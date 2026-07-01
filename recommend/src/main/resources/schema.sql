CREATE TABLE loan_offer (
    id BIGINT PRIMARY KEY,
    bank_name VARCHAR(255),
    interest_rate DOUBLE,
    max_tenure_months INT,
    min_credit_score INT,
    processing_fee_pct DOUBLE,
    max_loan_amount BIGINT
);
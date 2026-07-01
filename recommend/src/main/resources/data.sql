-- Sample bank loan offers seed data
MERGE INTO loan_offer (id, bank_name, interest_rate, max_tenure_months, min_credit_score, processing_fee_pct, max_loan_amount) KEY(id) VALUES
(1, 'HDFC Bank',       8.75, 84, 700, 0.50, 5000000),
(2, 'SBI',             8.65, 84, 680, 0.40, 5000000),
(3, 'ICICI Bank',      8.85, 84, 720, 0.50, 5000000),
(4, 'Axis Bank',       9.10, 72, 700, 0.75, 4000000),
(5, 'Kotak Mahindra',  9.25, 72, 680, 0.50, 3500000),
(6, 'Bajaj Finserv',   9.99, 60, 650, 1.00, 3000000);
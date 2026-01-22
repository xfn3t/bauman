-- 10. Для каждого счета посчитать: количество операций и сумму списаний (withdrawal + transfer_out + fee)
WITH transaction_summary AS (
    SELECT
        account_id,
        COUNT(*) as total_transactions,
        SUM(amount) FILTER (WHERE txn_type IN ('withdrawal', 'transfer_out', 'fee')) as total_debits,
        SUM(amount) FILTER (WHERE txn_type IN ('deposit', 'transfer_in')) as total_credits
    FROM transactions
    GROUP BY account_id
)
SELECT
    a.account_id,
    c.full_name,
    COALESCE(ts.total_transactions, 0) as total_transactions,
    COALESCE(ts.total_debits, 0.00) as total_debits,
    COALESCE(ts.total_credits, 0.00) as total_credits
FROM accounts a
JOIN clients c ON a.client_id = c.client_id
LEFT JOIN transaction_summary ts ON a.account_id = ts.account_id
ORDER BY total_transactions DESC;
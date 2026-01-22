-- 6. Топ-5 клиентов по сумме всех операций (оборот) за 2025 год
WITH client_turnover AS (
    SELECT
        a.client_id,
        COALESCE(SUM(t.amount), 0) AS total_turnover
    FROM accounts a
    LEFT JOIN transactions t
      ON a.account_id = t.account_id
      AND t.txn_date >= TIMESTAMP '2025-01-01 00:00:00'
      AND t.txn_date < TIMESTAMP '2026-01-01 00:00:00'
    GROUP BY a.client_id
)
SELECT
    c.client_id,
    c.full_name,
    ct.total_turnover
FROM clients c
JOIN client_turnover ct ON c.client_id = ct.client_id
ORDER BY ct.total_turnover DESC
LIMIT 5;

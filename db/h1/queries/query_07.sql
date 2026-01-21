-- 7. Определить "активность клиента" по количеству операций за последние 90 дней
WITH client_activity AS (
    SELECT
        c.client_id,
        c.full_name,
        COUNT(t.transaction_id) as operations_last_90_days
    FROM clients c
    LEFT JOIN accounts a ON c.client_id = a.client_id
    LEFT JOIN transactions t ON a.account_id = t.account_id
        AND t.txn_date >= CURRENT_DATE - INTERVAL '90 days'
    GROUP BY c.client_id, c.full_name
)
SELECT
    client_id,
    full_name,
    operations_last_90_days,
    CASE
        WHEN operations_last_90_days = 0 THEN 'inactive'
        WHEN operations_last_90_days <= 5 THEN 'low'
        WHEN operations_last_90_days <= 20 THEN 'medium'
        ELSE 'high'
    END as activity_level
FROM client_activity
ORDER BY operations_last_90_days DESC;
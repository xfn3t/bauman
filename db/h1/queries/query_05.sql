-- 5. Найти счета, у которых сумма входящих операций (deposit + transfer_in) выше среднего по банку
WITH account_incoming AS (
    SELECT
        account_id,
        SUM(amount) AS total_incoming
    FROM transactions
    WHERE txn_type IN ('deposit', 'transfer_in')
    GROUP BY account_id
)
SELECT
    a.account_id,
    c.full_name,
    ai.total_incoming
FROM account_incoming ai
JOIN accounts a ON ai.account_id = a.account_id
JOIN clients c ON a.client_id = c.client_id
WHERE ai.total_incoming > (
    SELECT COALESCE(AVG(total_incoming), 0) FROM account_incoming
)
ORDER BY ai.total_incoming DESC;

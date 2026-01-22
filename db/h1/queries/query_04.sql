-- 4. Найти клиентов, у которых больше 2 активных счетов
SELECT
    c.client_id,
    c.full_name,
    COUNT(a.account_id) as active_accounts_count
FROM clients c
JOIN accounts a ON c.client_id = a.client_id
WHERE a.status = 'active'
GROUP BY c.client_id, c.full_name
HAVING COUNT(a.account_id) > 2
ORDER BY active_accounts_count DESC;
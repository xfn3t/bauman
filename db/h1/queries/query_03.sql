-- 3. Вывести всех клиентов и количество их счетов (включая 0)
SELECT
    c.client_id,
    c.full_name,
    COUNT(a.account_id) as accounts_count
FROM clients c
LEFT JOIN accounts a ON c.client_id = a.client_id
GROUP BY c.client_id, c.full_name
ORDER BY accounts_count DESC, c.full_name;
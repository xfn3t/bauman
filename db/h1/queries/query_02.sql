-- 2. Вывести ФИО клиента, тип счета, валюту и статус счета
SELECT
    c.full_name,
    a.account_type,
    a.currency,
    a.status
FROM accounts a
JOIN clients c ON a.client_id = c.client_id
ORDER BY c.full_name;
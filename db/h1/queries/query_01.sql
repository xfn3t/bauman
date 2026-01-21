-- 1. Найти активные счета в EUR, открытые после 2024-01-01, отсортировать по дате открытия
SELECT
    account_id,
    client_id,
    account_type,
    currency,
    opened_at,
    status
FROM accounts
WHERE currency = 'EUR'
  AND status = 'active'
  AND opened_at > DATE '2024-01-01'
ORDER BY opened_at ASC;
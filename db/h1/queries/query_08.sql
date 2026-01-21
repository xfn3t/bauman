-- 8. Найти кредиты, по которым сумма успешных платежей < 50% от principal
SELECT
    l.loan_id,
    c.full_name,
    l.principal,
    COALESCE(SUM(lp.amount), 0) as total_paid,
    ROUND((COALESCE(SUM(lp.amount), 0) * 100.0 / l.principal), 2) as paid_percentage
FROM loans l
    JOIN clients c ON l.client_id = c.client_id
    LEFT JOIN loan_payments lp ON l.loan_id = lp.loan_id AND lp.status = 'success'
WHERE l.status = 'active'
GROUP BY l.loan_id, c.full_name, l.principal
HAVING COALESCE(SUM(lp.amount), 0) < l.principal * 0.5
ORDER BY paid_percentage ASC;
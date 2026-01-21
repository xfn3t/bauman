-- 9. Показать все активные карты и кому они принадлежат (ФИО, account_id, срок действия)
SELECT
    cd.card_id,
    cl.full_name,
    a.account_id,
    a.account_type,
    cd.card_type,
    cd.issued_at,
    cd.expires_at,
    cd.status,
    CASE
        WHEN cd.expires_at < CURRENT_DATE THEN 'EXPIRED'
        WHEN cd.expires_at <= CURRENT_DATE + INTERVAL '30 days' THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as validity_status
FROM cards cd
JOIN accounts a ON cd.account_id = a.account_id
JOIN clients cl ON a.client_id = cl.client_id
WHERE cd.status = 'active'
ORDER BY cd.expires_at;
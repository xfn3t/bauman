CREATE OR REPLACE VIEW messenger.v_partitions AS
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname || '.' || tablename)) as size,
    pg_table_size(schemaname || '.' || tablename) as bytes,
    pg_total_relation_size(schemaname || '.' || tablename) as total_bytes,
    CASE
        WHEN c.relispartition THEN pg_get_expr(c.relpartbound, c.oid)
        ELSE 'MAIN TABLE'
    END as partition_range,
    c.reltuples::bigint as estimated_rows
FROM pg_tables t
JOIN pg_class c ON c.relname = t.tablename AND c.relnamespace = t.schemaname::regnamespace
WHERE t.schemaname = 'messenger'
  AND t.tablename LIKE 'messages%'
ORDER BY pg_total_relation_size(schemaname || '.' || tablename) DESC;

CREATE OR REPLACE VIEW messenger.v_chat_stats AS
SELECT
    c.id,
    c.type,
    c.title,
    COUNT(DISTINCT cm.user_id) FILTER (WHERE cm.left_at IS NULL) as active_members,
    COUNT(DISTINCT cm.user_id) as total_members,
    COUNT(m.id) as message_count,
    MAX(m.created_at) as last_message_at,
    COUNT(DISTINCT m.sender_id) as active_senders,
    ROUND(AVG(cm.unread_count)::numeric, 2) as avg_unread_count
FROM messenger.chats c
LEFT JOIN messenger.chat_members cm ON c.id = cm.chat_id
LEFT JOIN messenger.messages m ON c.id = m.chat_id
GROUP BY c.id, c.type, c.title
ORDER BY last_message_at DESC NULLS LAST;

CREATE OR REPLACE VIEW messenger.v_user_stats AS
SELECT
    u.id,
    u.username,
    u.email,
    u.language,
    u.theme,
    COUNT(DISTINCT cm.chat_id) FILTER (WHERE cm.left_at IS NULL) as active_chats,
    COUNT(DISTINCT cm.chat_id) as total_chats,
    COUNT(m.id) as messages_sent,
    COUNT(DISTINCT m.chat_id) as chats_with_messages,
    MAX(m.created_at) as last_message_sent,
    SUM(COALESCE(a.file_size, 0)) as total_attachment_size
FROM messenger.users u
LEFT JOIN messenger.chat_members cm ON u.id = cm.user_id
LEFT JOIN messenger.messages m ON u.id = m.sender_id
LEFT JOIN messenger.attachments a ON m.id = a.message_id
WHERE u.deleted_at IS NULL
GROUP BY u.id, u.username, u.email, u.language, u.theme;
SELECT messenger.init_partitions(2);

INSERT INTO messenger.users (username, first_name, email, password_hash, language, theme)
VALUES (
    'adm',
    'Administrator',
    'admin@example.com',
    '$2a$12$Y4mC6Hv2qJQ5p5p5p5p5p.5p5p5p5p5p5p5p5p5p5p5p5p5p5p5p',
    'en-US',
    'dark'
) ON CONFLICT DO NOTHING;

INSERT INTO messenger.chats (type, title, is_public, created_by)
VALUES (
    'group',
    'General Chat',
    TRUE,
    (SELECT id FROM messenger.users WHERE username = 'admin')
) ON CONFLICT DO NOTHING;

INSERT INTO messenger.chat_members (chat_id, user_id, role)
SELECT
    c.id,
    u.id,
    'admin'
FROM messenger.chats c, messenger.users u
WHERE c.title = 'General Chat'
  AND u.username = 'admin'
ON CONFLICT DO NOTHING;
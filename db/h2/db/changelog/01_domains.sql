CREATE DOMAIN messenger.email_domain AS VARCHAR(255)
CHECK (VALUE ~ '^[A-Za-z0-9._%+-]+@(?:[A-Za-z0-9-]+\.)+[A-Za-z]{2,}$');

CREATE DOMAIN messenger.phone_domain AS VARCHAR(20)
CHECK (VALUE ~ '^\+?[1-9]\d{6,14}$');

CREATE DOMAIN messenger.username_domain AS VARCHAR(50)
CHECK (
    VALUE ~ '^[a-zA-Z0-9_]{3,50}$'
    AND VALUE NOT ILIKE '%admin%'
    AND VALUE NOT ILIKE '%support%'
    AND VALUE NOT ILIKE '%system%'
);

CREATE DOMAIN messenger.hex_color AS CHAR(7)
CHECK (VALUE ~ '^#[0-9A-Fa-f]{6}$');

CREATE DOMAIN messenger.url_domain AS TEXT
CHECK (VALUE ~ '^https?://[^\s/$.?#].[^\s]*$' OR VALUE IS NULL);

CREATE DOMAIN messenger.password_hash_domain AS TEXT
CHECK (
    VALUE ~ '^\$2[aby]\$'
    OR VALUE ~ '^\$argon2'
    OR VALUE ~ '^\$scrypt\$'
    OR LENGTH(VALUE) >= 60
);

CREATE DOMAIN messenger.chat_title_domain AS VARCHAR(255)
CHECK (LENGTH(TRIM(VALUE)) BETWEEN 1 AND 255 AND VALUE !~ '^\s*$');

CREATE DOMAIN messenger.message_text_domain AS TEXT
CHECK (LENGTH(TRIM(VALUE)) BETWEEN 1 AND 10000 AND VALUE !~ '^\s*$');

CREATE DOMAIN messenger.language_code AS CHAR(5)
CHECK (VALUE ~ '^[a-z]{2}$' OR VALUE ~ '^[a-z]{2}-[A-Z]{2}$');

CREATE DOMAIN messenger.theme_domain AS VARCHAR(20)
CHECK (VALUE IN ('light', 'dark', 'auto', 'oled'));

CREATE DOMAIN messenger.mime_type_domain AS VARCHAR(100)
CHECK (VALUE ~ '^[a-z]+/[a-z0-9.+*-]+$' OR VALUE ~ '^application/[a-z0-9.+*-]+$');

CREATE DOMAIN messenger.time_of_day AS TIME
CHECK (VALUE BETWEEN '00:00:00' AND '23:59:59');

CREATE DOMAIN messenger.timezone_domain AS VARCHAR(50)
CHECK (
    VALUE IN ('UTC', 'Europe/Moscow', 'America/New_York', 'Asia/Tokyo')
    OR VALUE ~ '^[A-Z][a-z]+/[A-Z][a-z]+$'
);

CREATE DOMAIN messenger.version_domain AS VARCHAR(20)
CHECK (VALUE ~ '^\d+\.\d+\.\d+$');
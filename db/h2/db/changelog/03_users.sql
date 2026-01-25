CREATE TABLE messenger.users (
    id BIGSERIAL PRIMARY KEY,
    username messenger.username_domain UNIQUE,
    phone messenger.phone_domain UNIQUE,
    email messenger.email_domain UNIQUE,
    first_name VARCHAR(100) NOT NULL CHECK (LENGTH(TRIM(first_name)) >= 1),
    last_name VARCHAR(100),
    avatar_url messenger.url_domain,
    status messenger.user_status_enum DEFAULT 'offline',
    last_seen TIMESTAMPTZ,
    password_hash messenger.password_hash_domain NOT NULL,
    password_changed_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    theme messenger.theme_domain DEFAULT 'light',
    language messenger.language_code DEFAULT 'en-US',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT users_at_least_one_identifier CHECK (
        (phone IS NOT NULL)::INTEGER +
        (email IS NOT NULL)::INTEGER +
        (username IS NOT NULL)::INTEGER >= 1
    ),
    CONSTRAINT users_name_length CHECK (
        LENGTH(CONCAT(TRIM(first_name), ' ', COALESCE(TRIM(last_name), ''))) BETWEEN 2 AND 200
    )
);

CREATE INDEX idx_users_phone_active ON messenger.users(phone) WHERE phone IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_users_email_active ON messenger.users(email) WHERE email IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_users_username_active ON messenger.users(username) WHERE username IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_users_status ON messenger.users(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_last_seen ON messenger.users(last_seen) WHERE last_seen IS NOT NULL;
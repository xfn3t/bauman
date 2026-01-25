CREATE TABLE messenger.message_reactions (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    message_created_at TIMESTAMPTZ NOT NULL,
    user_id BIGINT NOT NULL REFERENCES messenger.users(id),
    emoji VARCHAR(10) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (message_id, message_created_at, user_id, emoji),
    FOREIGN KEY (message_id, message_created_at)
        REFERENCES messenger.messages(id, created_at) ON DELETE CASCADE
);
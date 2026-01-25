CREATE TABLE messenger.messages (
    id BIGSERIAL,
    chat_id BIGINT NOT NULL REFERENCES messenger.chats(id),
    sender_id BIGINT NOT NULL REFERENCES messenger.users(id),
    type messenger.message_type_enum NOT NULL DEFAULT 'text',
    text messenger.message_text_domain,
    reply_to_id BIGINT,
    reply_to_created_at TIMESTAMPTZ,
    is_edited BOOLEAN DEFAULT FALSE,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_at),
    CONSTRAINT fk_messages_reply
        FOREIGN KEY (reply_to_id, reply_to_created_at)
        REFERENCES messenger.messages(id, created_at)
        ON DELETE SET NULL,
    CONSTRAINT messages_valid_text CHECK (
        (type = 'text' AND text IS NOT NULL) OR (type != 'text')
    ),
    CONSTRAINT messages_created_updated CHECK (created_at <= updated_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE messenger.messages_default PARTITION OF messenger.messages DEFAULT;
CREATE TABLE messenger.chats (
    id BIGSERIAL PRIMARY KEY,
    type messenger.chat_type_enum NOT NULL,
    title messenger.chat_title_domain,
    description TEXT CHECK (LENGTH(COALESCE(description, '')) <= 1000),
    avatar_url messenger.url_domain,
    is_public BOOLEAN DEFAULT FALSE,
    max_members INTEGER DEFAULT 500 CHECK (max_members BETWEEN 2 AND 100000),
    created_by BIGINT REFERENCES messenger.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    archived_at TIMESTAMPTZ,
    CONSTRAINT chats_title_required_for_group CHECK (
        (type != 'private' AND title IS NOT NULL) OR (type = 'private')
    ),
    CONSTRAINT chats_public_only_groups CHECK (
        (is_public = TRUE AND type IN ('group', 'channel')) OR (is_public = FALSE)
    )
);

CREATE INDEX idx_chats_type ON messenger.chats(type);
CREATE INDEX idx_chats_public ON messenger.chats(is_public) WHERE is_public = TRUE;
CREATE INDEX idx_chats_updated ON messenger.chats(updated_at DESC);
CREATE INDEX idx_chats_created_by ON messenger.chats(created_by) WHERE created_by IS NOT NULL;
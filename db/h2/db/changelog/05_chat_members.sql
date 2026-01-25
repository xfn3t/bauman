CREATE TABLE messenger.chat_members (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL REFERENCES messenger.chats(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES messenger.users(id) ON DELETE CASCADE,
    role messenger.user_role_enum DEFAULT 'member',
    notifications_enabled BOOLEAN DEFAULT TRUE,
    mute_until TIMESTAMPTZ,
    joined_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_read_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    unread_count INTEGER DEFAULT 0 CHECK (unread_count >= 0),
    left_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chat_members_valid_dates CHECK (joined_at <= COALESCE(left_at, CURRENT_TIMESTAMP)),
    CONSTRAINT chat_members_mute_future CHECK (mute_until IS NULL OR mute_until > CURRENT_TIMESTAMP)
);

CREATE UNIQUE INDEX idx_chat_members_active ON messenger.chat_members (chat_id, user_id) WHERE left_at IS NULL;
CREATE INDEX idx_chat_members_user ON messenger.chat_members(user_id) WHERE left_at IS NULL;
CREATE INDEX idx_chat_members_chat ON messenger.chat_members(chat_id) WHERE left_at IS NULL;
CREATE INDEX idx_chat_members_unread ON messenger.chat_members(unread_count) WHERE unread_count > 0 AND left_at IS NULL;
CREATE INDEX idx_chat_members_role ON messenger.chat_members(chat_id, role) WHERE left_at IS NULL;
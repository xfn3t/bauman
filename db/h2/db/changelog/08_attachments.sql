CREATE TABLE messenger.attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    message_id BIGINT NOT NULL,
    message_created_at TIMESTAMPTZ NOT NULL,
    file_name VARCHAR(255) NOT NULL CHECK (LENGTH(file_name) >= 1),
    file_size BIGINT NOT NULL CHECK (file_size > 0),
    mime_type messenger.mime_type_domain NOT NULL,
    storage_url messenger.url_domain NOT NULL,
    thumbnail_url messenger.url_domain,
    duration INTEGER CHECK (duration IS NULL OR duration > 0),
    width INTEGER CHECK (width IS NULL OR width > 0),
    height INTEGER CHECK (height IS NULL OR height > 0),
    uploaded_by BIGINT NOT NULL REFERENCES messenger.users(id),
    uploaded_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at TIMESTAMPTZ,
    CONSTRAINT fk_attachments_message
        FOREIGN KEY (message_id, message_created_at)
        REFERENCES messenger.messages(id, created_at)
        ON DELETE CASCADE,
    CONSTRAINT attachments_valid_expiry CHECK (expires_at IS NULL OR expires_at > uploaded_at),
    CONSTRAINT attachments_media_dimensions CHECK (
        (width IS NULL AND height IS NULL) OR (width IS NOT NULL AND height IS NOT NULL)
    )
);

CREATE INDEX idx_attachments_message ON messenger.attachments(message_id, message_created_at);
CREATE INDEX idx_attachments_uploaded_by ON messenger.attachments(uploaded_by);
CREATE INDEX idx_attachments_expires ON messenger.attachments(expires_at) WHERE expires_at IS NOT NULL;
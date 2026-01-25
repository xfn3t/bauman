CREATE OR REPLACE FUNCTION messenger.update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION messenger.increment_unread_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE messenger.chat_members cm
    SET
        unread_count = unread_count + 1,
        updated_at = CURRENT_TIMESTAMP
    WHERE cm.chat_id = NEW.chat_id
      AND cm.user_id != NEW.sender_id
      AND cm.left_at IS NULL
      AND (cm.mute_until IS NULL OR cm.mute_until < CURRENT_TIMESTAMP);

    UPDATE messenger.chats
    SET updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.chat_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated
    BEFORE UPDATE ON messenger.users
    FOR EACH ROW EXECUTE FUNCTION messenger.update_updated_at();

CREATE TRIGGER trg_chats_updated
    BEFORE UPDATE ON messenger.chats
    FOR EACH ROW EXECUTE FUNCTION messenger.update_updated_at();

CREATE TRIGGER trg_chat_members_updated
    BEFORE UPDATE ON messenger.chat_members
    FOR EACH ROW EXECUTE FUNCTION messenger.update_updated_at();

CREATE TRIGGER trg_messages_updated
    BEFORE UPDATE ON messenger.messages
    FOR EACH ROW EXECUTE FUNCTION messenger.update_updated_at();

CREATE TRIGGER trg_increment_unread
    AFTER INSERT ON messenger.messages
    FOR EACH ROW EXECUTE FUNCTION messenger.increment_unread_count();

CREATE TRIGGER trg_messages_partition_check
    BEFORE INSERT ON messenger.messages
    FOR EACH ROW EXECUTE FUNCTION messenger.messages_partition_trigger();

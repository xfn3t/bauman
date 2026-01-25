CREATE TYPE messenger.chat_type_enum AS ENUM ('private', 'group', 'channel');
CREATE TYPE messenger.message_type_enum AS ENUM ('text', 'image', 'video', 'audio', 'file', 'sticker', 'poll', 'location');
CREATE TYPE messenger.user_role_enum AS ENUM ('owner', 'admin', 'member', 'guest');
CREATE TYPE messenger.user_status_enum AS ENUM ('online', 'offline', 'away', 'busy');
CREATE OR REPLACE FUNCTION messenger.create_message_partition(year_value INTEGER)
RETURNS TEXT AS $$
DECLARE
    partition_name TEXT;
    year_start DATE;
    year_end DATE;
BEGIN
    partition_name := 'messages_y' || year_value;

    IF EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'messenger' AND tablename = partition_name) THEN
        RETURN partition_name;
    END IF;

    year_start := DATE(year_value || '-01-01');
    year_end := DATE((year_value + 1) || '-01-01');

    EXECUTE format(
        'CREATE TABLE messenger.%I PARTITION OF messenger.messages
         FOR VALUES FROM (%L) TO (%L)',
        partition_name, year_start, year_end
    );

    EXECUTE format(
        'CREATE INDEX idx_%s_chat_created ON messenger.%I (chat_id, created_at DESC)',
        partition_name, partition_name
    );

    EXECUTE format(
        'CREATE INDEX idx_%s_sender ON messenger.%I (sender_id, created_at DESC)',
        partition_name, partition_name
    );

    EXECUTE format(
        'CREATE INDEX idx_%s_text ON messenger.%I USING GIN (to_tsvector(''english'', text))
         WHERE text IS NOT NULL',
        partition_name, partition_name
    );

    EXECUTE format(
        'CREATE INDEX idx_%s_reply ON messenger.%I (reply_to_id)
         WHERE reply_to_id IS NOT NULL',
        partition_name, partition_name
    );

    RAISE NOTICE 'Created partition messenger.%', partition_name;
    RETURN partition_name;
EXCEPTION WHEN OTHERS THEN
    RAISE WARNING 'Failed to create partition %: %', partition_name, SQLERRM;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION messenger.init_partitions(years_ahead INTEGER DEFAULT 2)
RETURNS VOID AS $$
DECLARE
    current_year INTEGER;
    year_offset INTEGER;
BEGIN
    current_year := EXTRACT(YEAR FROM CURRENT_DATE);
    FOR year_offset IN -1..years_ahead LOOP
        PERFORM messenger.create_message_partition(current_year + year_offset);
    END LOOP;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION messenger.messages_partition_trigger()
RETURNS TRIGGER AS $$
DECLARE
    partition_name TEXT;
    message_year INTEGER;
    insert_result TEXT;
BEGIN
    -- Определяем год из created_at
    message_year := EXTRACT(YEAR FROM NEW.created_at);
    partition_name := 'messages_y' || message_year;

    -- Если вставляем в дефолтную партицию
    IF TG_TABLE_NAME = 'messages_default' THEN
        -- Пытаемся создать партицию (если уже есть - функция вернет имя)
        partition_name := messenger.create_message_partition(message_year);

        IF partition_name IS NULL THEN
            -- Если создать не удалось, оставляем в default
            RAISE WARNING 'Failed to create partition for year %, inserting into default', message_year;
            RETURN NEW;
        END IF;

        -- Пытаемся вставить в партицию
        BEGIN
            EXECUTE format('INSERT INTO messenger.%I VALUES ($1.*)', partition_name)
            USING NEW;

            -- Успешно вставили в партицию - отменяем вставку в default
            RETURN NULL;

        EXCEPTION WHEN OTHERS THEN
            -- Если вставка в партицию не удалась, вставляем в default
            RAISE WARNING 'Failed to insert into partition %: %, inserting into default',
                          partition_name, SQLERRM;
            RETURN NEW;
        END;
    END IF;

    -- Если вставляем в конкретную партицию, проверяем соответствие даты
    IF TG_TABLE_NAME LIKE 'messages_y%' THEN
        IF EXTRACT(YEAR FROM NEW.created_at) !=
           NULLIF(SUBSTRING(TG_TABLE_NAME FROM 'messages_y(\d{4})'), '')::INTEGER THEN
            RAISE EXCEPTION
                'Cannot insert message with created_at=% into partition %',
                NEW.created_at, TG_TABLE_NAME;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION messenger.create_private_chat(
    user1_id BIGINT,
    user2_id BIGINT,
    created_by BIGINT DEFAULT NULL
)
RETURNS BIGINT AS $$
DECLARE
    v_chat_id BIGINT;
    v_exists BIGINT;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM messenger.users WHERE id = user1_id AND deleted_at IS NULL) OR
       NOT EXISTS (SELECT 1 FROM messenger.users WHERE id = user2_id AND deleted_at IS NULL) THEN
        RAISE EXCEPTION 'One or both users do not exist';
    END IF;

    IF user1_id = user2_id THEN
        RAISE EXCEPTION 'Cannot create private chat with yourself';
    END IF;

    SELECT c.id INTO v_exists
    FROM messenger.chats c
    WHERE c.type = 'private'
      AND EXISTS (SELECT 1 FROM messenger.chat_members cm1 WHERE cm1.chat_id = c.id AND cm1.user_id = user1_id AND cm1.left_at IS NULL)
      AND EXISTS (SELECT 1 FROM messenger.chat_members cm2 WHERE cm2.chat_id = c.id AND cm2.user_id = user2_id AND cm2.left_at IS NULL)
      AND (SELECT COUNT(*) FROM messenger.chat_members WHERE chat_id = c.id AND left_at IS NULL) = 2
    LIMIT 1;

    IF v_exists IS NOT NULL THEN
        RETURN v_exists;
    END IF;

    INSERT INTO messenger.chats (type, created_by)
    VALUES ('private', COALESCE(created_by, user1_id))
    RETURNING id INTO v_chat_id;

    INSERT INTO messenger.chat_members (chat_id, user_id)
    VALUES (v_chat_id, user1_id), (v_chat_id, user2_id);

    RETURN v_chat_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION messenger.cleanup_old_messages(
    older_than_months INTEGER DEFAULT 12,
    batch_size INTEGER DEFAULT 1000
)
RETURNS TABLE(deleted_count BIGINT, partition_name TEXT) AS $$
DECLARE
    partition RECORD;
    v_deleted BIGINT;
    v_total_deleted BIGINT := 0;
BEGIN
    FOR partition IN
        SELECT schemaname, tablename
        FROM pg_tables
        WHERE schemaname = 'messenger' AND tablename LIKE 'messages_y%'
    LOOP
        EXECUTE format(
            'WITH deleted AS (
                DELETE FROM %I.%I
                WHERE created_at < CURRENT_TIMESTAMP - INTERVAL ''%s months''
                LIMIT %s
                RETURNING 1
            ) SELECT COUNT(*) FROM deleted',
            partition.schemaname, partition.tablename,
            older_than_months, batch_size
        ) INTO v_deleted;

        v_total_deleted := v_total_deleted + v_deleted;
        partition_name := partition.tablename;
        deleted_count := v_deleted;
        RETURN NEXT;

        IF v_deleted > 0 THEN
            EXECUTE format('VACUUM ANALYZE %I.%I', partition.schemaname, partition.tablename);
        END IF;
    END LOOP;

    partition_name := 'TOTAL';
    deleted_count := v_total_deleted;
    RETURN NEXT;
END;
$$ LANGUAGE plpgsql;
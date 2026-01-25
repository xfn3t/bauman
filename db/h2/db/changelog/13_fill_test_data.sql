DO $$
DECLARE
    -- Переменные для хранения ID
    user_ids BIGINT[];
    chat_ids BIGINT[];
    message_ids BIGINT[];

    -- Вспомогательные переменные
    i INTEGER;
    j INTEGER;
    user_count INTEGER := 50; -- Количество пользователей
    chat_count INTEGER := 20; -- Количество чатов
    messages_per_chat INTEGER := 100; -- Сообщений в каждом чате

    -- Данные для генерации (исправлены для соответствия доменам)
    first_names_en TEXT[] := ARRAY['Alex', 'Maria', 'John', 'Kate', 'Dmitry', 'Anna', 'Sergey', 'Olga', 'Andrew', 'Natalie', 'Max', 'Helen', 'Paul', 'Tatyana', 'Vladimir', 'Julia', 'Alexander', 'Irina', 'Nicholas', 'Svetlana'];
    first_names_ru TEXT[] := ARRAY['Алексей', 'Мария', 'Иван', 'Екатерина', 'Дмитрий', 'Анна', 'Сергей', 'Ольга', 'Андрей', 'Наталья', 'Максим', 'Елена', 'Павел', 'Татьяна', 'Владимир', 'Юлия', 'Александр', 'Ирина', 'Николай', 'Светлана'];
    last_names_en TEXT[] := ARRAY['Ivanov', 'Petrov', 'Sidorov', 'Smirnov', 'Kuznetsov', 'Popov', 'Vasiliev', 'Sokolov', 'Mikhailov', 'Novikov', 'Fedorov', 'Morozov', 'Volkov', 'Alexeev', 'Lebedev', 'Semenov', 'Egorov', 'Pavlov', 'Kozlov', 'Stepanov'];
    last_names_ru TEXT[] := ARRAY['Иванов', 'Петров', 'Сидоров', 'Смирнов', 'Кузнецов', 'Попов', 'Васильев', 'Соколов', 'Михайлов', 'Новиков', 'Федоров', 'Морозов', 'Волков', 'Алексеев', 'Лебедев', 'Семенов', 'Егоров', 'Павлов', 'Козлов', 'Степанов'];

    domains TEXT[] := ARRAY['gmail.com', 'yandex.ru', 'mail.ru', 'outlook.com', 'yahoo.com'];
    domains_en TEXT[] := ARRAY['gmail.com', 'outlook.com', 'yahoo.com', 'hotmail.com'];

    -- Роли для распределения (соответствуют user_role_enum)
    roles messenger.user_role_enum[] := ARRAY['owner', 'admin', 'member', 'guest'];
    statuses messenger.user_status_enum[] := ARRAY['online', 'offline', 'away', 'busy'];
    themes messenger.theme_domain[] := ARRAY['light', 'dark', 'auto', 'oled'];

    -- ЯЗЫКИ: ТОЛЬКО СООТВЕТСТВУЮЩИЕ ДОМЕНУ language_code
    languages TEXT[] := ARRAY['en-US', 'ru-RU', 'fr-FR', 'de-DE', 'es-ES', 'it-IT'];

    -- Для генерации сообщений
    messages_en TEXT[] := ARRAY[
        'Hello! How are you?',
        'Whats new?',
        'Meeting at 18:00',
        'Great job!',
        'Please send me the file',
        'When will the next release be?',
        'Lets discuss at the meeting',
        'I have a question',
        'Thanks for your help!',
        'This is great news!',
        'Need to check the documents',
        'When can we call?',
        'Sent to email',
        'Look at the presentation',
        'Very interesting idea',
        'Need your help with the project',
        'Tomorrow at 10:00 planning meeting',
        'Report is ready',
        'Can we reschedule the meeting?',
        'What suggestions do you have?'
    ];

    messages_ru TEXT[] := ARRAY[
        'Привет! Как дела?',
        'Что нового?',
        'Встречаемся в 18:00',
        'Отличная работа!',
        'Отправьте мне файл, пожалуйста',
        'Когда будет следующий релиз?',
        'Обсудим на собрании',
        'У меня есть вопрос',
        'Спасибо за помощь!',
        'Это отличная новость!',
        'Нужно проверить документы',
        'Когда сможешь созвониться?',
        'Отправил на почту',
        'Посмотрите презентацию',
        'Очень интересная идея',
        'Нужна ваша помощь с проектом',
        'Завтра в 10:00 на планерке',
        'Отчет готов',
        'Можно ли перенести встречу?',
        'Какие будут предложения?'
    ];

    -- Медиа файлы (соответствуют домену mime_type_domain)
    file_types TEXT[] := ARRAY['image/jpeg', 'image/png', 'video/mp4', 'audio/mpeg', 'application/pdf', 'application/zip'];
    file_names TEXT[] := ARRAY['presentation.pdf', 'photo.jpg', 'video.mp4', 'document.docx', 'music.mp3', 'screenshot.png', 'report.xlsx', 'archive.zip'];

    -- Для рандомного выбора
    temp_user_id BIGINT;
    temp_chat_id BIGINT;
    temp_message_id BIGINT;
    temp_created_at TIMESTAMPTZ;
    random_role messenger.user_role_enum;
    random_status messenger.user_status_enum;
    random_theme messenger.theme_domain;
    random_language TEXT;
    random_message TEXT;
    random_mime_type TEXT;
    random_file_name TEXT;
    chat_type messenger.chat_type_enum;

    -- Временные переменные
    temp_phone TEXT;
    temp_email TEXT;
    temp_username TEXT;
    temp_first_name TEXT;
    temp_last_name TEXT;

    -- Для генерации данных
    fn_idx INTEGER;
    ln_idx INTEGER;
    domain_idx INTEGER;
    use_english BOOLEAN;

BEGIN
    RAISE NOTICE 'Начинаем заполнение базы тестовыми данными...';


    RAISE NOTICE 'Создаем пользователей...';

    FOR i IN 1..user_count LOOP
        -- Решаем, использовать английские или русские имена
        use_english := random() > 0.5;

        -- Выбираем индексы для имени и фамилии
        fn_idx := floor(random() * 20) + 1;
        ln_idx := floor(random() * 20) + 1;

        -- Выбираем имя и фамилию
        IF use_english THEN
            temp_first_name := first_names_en[fn_idx];
            temp_last_name := last_names_en[ln_idx];
            domain_idx := floor(random() * array_length(domains_en, 1)) + 1;
        ELSE
            temp_first_name := first_names_ru[fn_idx];
            temp_last_name := last_names_ru[ln_idx];
            domain_idx := floor(random() * array_length(domains, 1)) + 1;
        END IF;

        -- Иногда не указываем фамилию
        IF random() > 0.85 THEN
            temp_last_name := NULL;
        END IF;

        -- Генерируем username (только латинские буквы, цифры и подчеркивание)
        IF temp_last_name IS NOT NULL THEN
            temp_username := LOWER(
                SUBSTRING(temp_first_name FROM 1 FOR 1) ||
                CASE
                    WHEN use_english THEN LOWER(REPLACE(temp_last_name, ' ', ''))
                    ELSE 'user' -- Для кириллицы используем общий префикс
                END ||
                floor(random() * 100)::text
            );
        ELSE
            temp_username := LOWER(
                REPLACE(temp_first_name, ' ', '') ||
                floor(random() * 1000)::text
            );
        END IF;

        -- Очищаем username от недопустимых символов
        temp_username := REGEXP_REPLACE(temp_username, '[^a-zA-Z0-9_]', '', 'g');

        -- Гарантируем минимальную длину
        IF LENGTH(temp_username) < 3 THEN
            temp_username := temp_username || 'user';
        END IF;

        -- Гарантируем максимальную длину
        IF LENGTH(temp_username) > 45 THEN
            temp_username := SUBSTRING(temp_username FROM 1 FOR 45);
        END IF;

        -- Добавляем уникальный суффикс
        temp_username := temp_username || '_' || i;

        -- Генерируем email (только латинские символы)
        IF use_english THEN
            temp_email := LOWER(
                REPLACE(temp_first_name, ' ', '') ||
                '.' ||
                CASE
                    WHEN temp_last_name IS NOT NULL THEN REPLACE(temp_last_name, ' ', '')
                    ELSE 'user'
                END ||
                floor(random() * 100)::text ||
                '@' || domains_en[domain_idx]
            );
        ELSE
            -- Для русских имен используем транслитерацию или простые email
            temp_email := 'user' || i || '_' || floor(random() * 10000)::text || '@' || domains[domain_idx];
        END IF;

        -- Очищаем email от недопустимых символов
        temp_email := REGEXP_REPLACE(temp_email, '[^a-zA-Z0-9@._%+-]', '', 'g');

        -- Проверяем, что email соответствует формату
        IF temp_email !~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
            -- Используем простой валидный email
            temp_email := 'user' || i || '_' || floor(random() * 100000)::text || '@example.com';
        END IF;

        -- Ограничиваем длину email
        IF LENGTH(temp_email) > 255 THEN
            temp_email := SUBSTRING(temp_email FROM 1 FOR 255);
        END IF;

        -- Генерируем телефон (не всем)
        IF random() > 0.3 THEN
            temp_phone := '+7' || (9000000000 + floor(random() * 1000000000))::text;
        ELSE
            temp_phone := NULL;
        END IF;

        -- Выбираем случайные настройки
        random_status := statuses[(i % array_length(statuses, 1)) + 1];
        random_theme := themes[(i % array_length(themes, 1)) + 1];
        random_language := languages[(i % array_length(languages, 1)) + 1];

        -- Вставляем пользователя
        BEGIN
            INSERT INTO messenger.users (
                username,
                email,
                phone,
                first_name,
                last_name,
                status,
                password_hash,
                theme,
                language,
                last_seen,
                avatar_url
            ) VALUES (
                temp_username,
                temp_email,
                temp_phone,
                temp_first_name,
                temp_last_name,
                random_status,
                '$2a$12$Y4mC6Hv2qJQ5p5p5p5p5p.5p5p5p5p5p5p5p5p5p5p5p5p5p5p5p', -- bcrypt от 'password'
                random_theme,
                random_language,
                CASE
                    WHEN random_status = 'online' THEN
                        CURRENT_TIMESTAMP - interval '5 minutes'
                    ELSE
                        CURRENT_TIMESTAMP - interval '1 day' * floor(random() * 30 + 1)
                END,
                CASE WHEN random() > 0.5 THEN
                    'https://api.dicebear.com/7.x/avatars/svg?seed=' || i::text
                ELSE NULL END
            )
            RETURNING id INTO temp_user_id;

            user_ids := array_append(user_ids, temp_user_id);

        EXCEPTION WHEN OTHERS THEN
            RAISE NOTICE 'Ошибка при создании пользователя %: %', temp_username, SQLERRM;
            -- Пропускаем этого пользователя
            CONTINUE;
        END;

        -- Прогресс
        IF i % 10 = 0 THEN
            RAISE NOTICE 'Создано % пользователей из %', i, user_count;
        END IF;
    END LOOP;

    -- Если не удалось создать ни одного пользователя, создаем хотя бы одного
    IF array_length(user_ids, 1) = 0 THEN
        INSERT INTO messenger.users (
            username,
            email,
            phone,
            first_name,
            last_name,
            status,
            password_hash,
            theme,
            language,
            last_seen
        ) VALUES (
            'test_user_1',
            'test1@example.com',
            '+79001234567',
            'Test',
            'User',
            'online',
            '$2a$12$Y4mC6Hv2qJQ5p5p5p5p5p.5p5p5p5p5p5p5p5p5p5p5p5p5p5p5p',
            'light',
            'en',
            CURRENT_TIMESTAMP
        )
        RETURNING id INTO temp_user_id;

        user_ids := array_append(user_ids, temp_user_id);
    END IF;

    RAISE NOTICE 'Создано % пользователей', array_length(user_ids, 1);

    -- ========================
    -- 2. СОЗДАЕМ ЧАТЫ
    -- ========================
    RAISE NOTICE 'Создаем чаты...';

    -- Личные чаты (30% от общего числа)
    FOR i IN 1..(chat_count * 0.3)::INTEGER LOOP
        -- Проверяем, что есть хотя бы 2 пользователя
        IF array_length(user_ids, 1) >= 2 THEN
            -- Выбираем двух разных пользователей
            DECLARE
                user1_idx INTEGER := floor(random() * array_length(user_ids, 1)) + 1;
                user2_idx INTEGER;
            BEGIN
                -- Гарантируем, что user2 отличается от user1
                LOOP
                    user2_idx := floor(random() * array_length(user_ids, 1)) + 1;
                    EXIT WHEN user2_idx != user1_idx;
                END LOOP;

                -- Создаем личный чат
                PERFORM messenger.create_private_chat(
                    user_ids[user1_idx],
                    user_ids[user2_idx],
                    user_ids[user1_idx]
                );
            END;
        END IF;
    END LOOP;

    -- Групповые чаты (50% от общего числа)
    FOR i IN 1..(chat_count * 0.5)::INTEGER LOOP
        BEGIN
            -- Создаем групповой чат
            INSERT INTO messenger.chats (
                type,
                title,
                description,
                is_public,
                max_members,
                avatar_url,
                created_by
            ) VALUES (
                'group',
                'Группа ' || i,
                CASE WHEN random() > 0.5 THEN
                    'Описание группы ' || i || '. Обсуждаем важные вопросы проекта.'
                ELSE NULL END,
                random() > 0.7, -- 30% публичных
                floor(random() * 100) + 10, -- от 10 до 110 участников
                CASE WHEN random() > 0.6 THEN
                    'https://api.dicebear.com/7.x/identicon/svg?seed=group' || i
                ELSE NULL END,
                user_ids[floor(random() * array_length(user_ids, 1)) + 1]
            )
            RETURNING id INTO temp_chat_id;

            chat_ids := array_append(chat_ids, temp_chat_id);

        EXCEPTION WHEN OTHERS THEN
            RAISE NOTICE 'Ошибка при создании группового чата: %', SQLERRM;
        END;
    END LOOP;

    -- Каналы (20% от общего числа)
    FOR i IN 1..(chat_count * 0.2)::INTEGER LOOP
        BEGIN
            INSERT INTO messenger.chats (
                type,
                title,
                description,
                is_public,
                max_members,
                avatar_url,
                created_by
            ) VALUES (
                'channel',
                'Канал ' || i,
                'Официальный канал №' || i,
                random() > 0.5, -- 50% публичных
                NULL, -- для каналов не ограничиваем
                CASE WHEN random() > 0.4 THEN
                    'https://api.dicebear.com/7.x/bottts/svg?seed=channel' || i
                ELSE NULL END,
                user_ids[floor(random() * array_length(user_ids, 1)) + 1]
            )
            RETURNING id INTO temp_chat_id;

            chat_ids := array_append(chat_ids, temp_chat_id);

        EXCEPTION WHEN OTHERS THEN
            RAISE NOTICE 'Ошибка при создании канала: %', SQLERRM;
        END;
    END LOOP;

    RAISE NOTICE 'Создано % чатов', array_length(chat_ids, 1);


    RAISE NOTICE 'Добавляем участников в чаты...';

    FOR i IN 1..array_length(chat_ids, 1) LOOP
        -- Определяем тип чата
        SELECT type INTO chat_type FROM messenger.chats WHERE id = chat_ids[i];

        -- Для каждого чата добавляем участников
        IF chat_type = 'private' THEN
            -- В личных чатах уже есть 2 участника (созданы функцией)
            CONTINUE;
        ELSIF chat_type = 'group' THEN
            -- В групповые чаты добавляем от 5 до 30 участников
            FOR j IN 1..LEAST(floor(random() * 25 + 5), array_length(user_ids, 1)) LOOP
                BEGIN
                    -- Выбираем случайного пользователя
                    temp_user_id := user_ids[floor(random() * array_length(user_ids, 1)) + 1];

                    -- Определяем роль
                    random_role := CASE
                        WHEN j = 1 THEN 'owner'  -- Первый - owner
                        WHEN j <= 3 AND random() > 0.5 THEN 'admin'  -- Следующие могут быть admin
                        WHEN random() > 0.9 THEN 'guest'  -- 10% гостей
                        ELSE 'member'  -- Остальные - участники
                    END;

                    -- Добавляем участника
                    INSERT INTO messenger.chat_members (
                        chat_id,
                        user_id,
                        role,
                        notifications_enabled,
                        mute_until,
                        joined_at,
                        last_read_at,
                        unread_count
                    ) VALUES (
                        chat_ids[i],
                        temp_user_id,
                        random_role,
                        random() > 0.2, -- 80% с уведомлениями
                        CASE WHEN random() > 0.9 THEN
                            CURRENT_TIMESTAMP + interval '1 hour' * floor(random() * 24 + 1)
                        ELSE NULL END,
                        CURRENT_TIMESTAMP - interval '1 day' * floor(random() * 30 + 1),
                        CURRENT_TIMESTAMP - interval '1 hour' * floor(random() * 24 + 1),
                        floor(random() * 50) -- непрочитанные сообщения
                    );
                EXCEPTION
                    WHEN unique_violation THEN
                        -- Пользователь уже в чате, пропускаем
                        CONTINUE;
                    WHEN OTHERS THEN
                        RAISE NOTICE 'Ошибка при добавлении участника: %', SQLERRM;
                END;
            END LOOP;
        ELSIF chat_type = 'channel' THEN
            -- В каналы добавляем много участников (от 20 до 100, но не больше чем есть пользователей)
            FOR j IN 1..LEAST(floor(random() * 80 + 20), array_length(user_ids, 1)) LOOP
                BEGIN
                    -- Выбираем случайного пользователя
                    temp_user_id := user_ids[floor(random() * array_length(user_ids, 1)) + 1];

                    -- Определяем роль для канала
                    random_role := CASE
                        WHEN j = 1 THEN 'owner'
                        WHEN j <= 5 THEN 'admin'
                        ELSE 'member'
                    END;

                    -- Добавляем участника
                    INSERT INTO messenger.chat_members (
                        chat_id,
                        user_id,
                        role,
                        notifications_enabled,
                        joined_at
                    ) VALUES (
                        chat_ids[i],
                        temp_user_id,
                        random_role,
                        random() > 0.3, -- 70% с уведомлениями
                        CURRENT_TIMESTAMP - interval '1 day' * floor(random() * 60 + 1)
                    );
                EXCEPTION
                    WHEN unique_violation THEN
                        -- Пользователь уже в чате, пропускаем
                        CONTINUE;
                    WHEN OTHERS THEN
                        RAISE NOTICE 'Ошибка при добавлении участника в канал: %', SQLERRM;
                END;
            END LOOP;
        END IF;

        -- Прогресс
        IF i % 5 = 0 THEN
            RAISE NOTICE 'Обработано % из % чатов', i, array_length(chat_ids, 1);
        END IF;
    END LOOP;

    RAISE NOTICE 'Участники добавлены';


    RAISE NOTICE 'Создаем сообщения в чатах...';

    -- Сначала получаем все активные чаты
    DECLARE
        active_chats CURSOR FOR
            SELECT c.id, c.type
            FROM messenger.chats c
            WHERE c.archived_at IS NULL
            ORDER BY c.id;
    BEGIN
        FOR chat_rec IN active_chats LOOP
            -- Для каждого чата создаем несколько сообщений
            FOR j IN 1..messages_per_chat LOOP
                -- Получаем случайного участника чата в качестве отправителя
                SELECT user_id INTO temp_user_id
                FROM messenger.chat_members
                WHERE chat_id = chat_rec.id
                  AND left_at IS NULL
                ORDER BY random()
                LIMIT 1;

                IF temp_user_id IS NULL THEN
                    CONTINUE;
                END IF;

                -- Выбираем случайное сообщение на русском или английском
                IF random() > 0.5 THEN
                    random_message := messages_ru[floor(random() * array_length(messages_ru, 1)) + 1];
                ELSE
                    random_message := messages_en[floor(random() * array_length(messages_en, 1)) + 1];
                END IF;

                -- Генерируем время сообщения
                temp_created_at := CURRENT_TIMESTAMP -
                    interval '1 day' * floor(random() * 90) -
                    interval '1 hour' * floor(random() * 24) -
                    interval '1 minute' * floor(random() * 60);

                BEGIN
                    -- Создаем сообщение
                    INSERT INTO messenger.messages (
                        chat_id,
                        sender_id,
                        type,
                        text,
                        is_edited,
                        is_pinned,
                        created_at
                    ) VALUES (
                        chat_rec.id,
                        temp_user_id,
                        'text',
                        random_message,
                        random() > 0.9, -- 10% отредактированных
                        random() > 0.95, -- 5% закрепленных
                        temp_created_at
                    )
                    RETURNING id INTO temp_message_id;

                    message_ids := array_append(message_ids, temp_message_id);

                EXCEPTION WHEN OTHERS THEN
                    RAISE NOTICE 'Ошибка при создании сообщения: %', SQLERRM;
                    CONTINUE;
                END;

                -- Прогресс для сообщений
                IF j % 50 = 0 THEN
                    RAISE NOTICE '  Чат %: создано % сообщений', chat_rec.id, j;
                END IF;
            END LOOP;

            -- Прогресс по чатам
            RAISE NOTICE 'Обработан чат % (тип: %)', chat_rec.id, chat_rec.type;
        END LOOP;
    END;

    RAISE NOTICE 'Создано сообщений: примерно %', messages_per_chat * array_length(chat_ids, 1);


    RAISE NOTICE 'Создаем вложения...';

    -- Создаем вложения для некоторых сообщений
    FOR i IN 1..LEAST(100, array_length(message_ids, 1)) LOOP
        -- Берем каждое 10-е сообщение
        IF i % 10 != 0 THEN
            CONTINUE;
        END IF;

        BEGIN
            -- Получаем данные сообщения
            SELECT m.id, m.created_at, m.sender_id
            INTO temp_message_id, temp_created_at, temp_user_id
            FROM messenger.messages m
            WHERE m.id = message_ids[i];

            IF temp_message_id IS NULL THEN
                CONTINUE;
            END IF;

            -- Выбираем тип файла
            random_mime_type := file_types[floor(random() * array_length(file_types, 1)) + 1];
            random_file_name := file_names[floor(random() * array_length(file_names, 1)) + 1];

            -- Создаем вложение
            INSERT INTO messenger.attachments (
                message_id,
                message_created_at,
                file_name,
                file_size,
                mime_type,
                storage_url,
                thumbnail_url,
                duration,
                width,
                height,
                uploaded_by
            ) VALUES (
                temp_message_id,
                temp_created_at,
                random_file_name,
                floor(random() * 10000000) + 1000, -- размер от 1KB до 10MB
                random_mime_type,
                'https://storage.example.com/files/' || uuid_generate_v4(),
                CASE WHEN random_mime_type LIKE 'image/%' THEN
                    'https://storage.example.com/thumbs/' || uuid_generate_v4()
                ELSE NULL END,
                CASE WHEN random_mime_type LIKE 'video/%' OR random_mime_type LIKE 'audio/%' THEN
                    floor(random() * 600) + 30 -- длительность 30-630 секунд
                ELSE NULL END,
                CASE WHEN random_mime_type LIKE 'image/%' OR random_mime_type LIKE 'video/%' THEN
                    floor(random() * 1920) + 640 -- ширина 640-2560
                ELSE NULL END,
                CASE WHEN random_mime_type LIKE 'image/%' OR random_mime_type LIKE 'video/%' THEN
                    floor(random() * 1080) + 480 -- высота 480-1560
                ELSE NULL END,
                temp_user_id
            );

        EXCEPTION WHEN OTHERS THEN
            RAISE NOTICE 'Ошибка при создании вложения: %', SQLERRM;
        END;

    END LOOP;
    RAISE NOTICE 'Вложения созданы';


    RAISE NOTICE 'Добавляем реакции на сообщения...';

    -- Популярные эмодзи для реакций
    DECLARE
           reaction_emojis TEXT[] := ARRAY['👍', '👎', '❤️', '🔥', '🎉', '😄', '😮', '😢', '😡', '🤔', '👏', '🙏', '🥳', '🤯', '🤬', '🥰', '🤩', '😎', '🥺', '😱'];
           emoji_count INTEGER := array_length(reaction_emojis, 1);
           reaction_count INTEGER := 0;
           current_user_id BIGINT;
                current_message_id BIGINT;
                current_created_at TIMESTAMPTZ;
                current_chat_id BIGINT;
                current_sender_id BIGINT;
                random_emoji TEXT;
                k INTEGER;
                l INTEGER;
            BEGIN
                -- Добавляем реакции на случайные сообщения
                FOR k IN 1..LEAST(300, array_length(message_ids, 1)) LOOP
                    -- Берем случайное сообщение
                    current_message_id := message_ids[floor(random() * array_length(message_ids, 1)) + 1];

                    -- Получаем данные сообщения
                    BEGIN
                        SELECT created_at, chat_id, sender_id
                        INTO current_created_at, current_chat_id, current_sender_id
                        FROM messenger.messages
                        WHERE id = current_message_id;

                        IF current_chat_id IS NULL THEN
                            CONTINUE;
                        END IF;
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            CONTINUE;
                    END;

                    -- Выбираем случайных участников чата для реакций (от 1 до 10 реакций на сообщение)
                    FOR l IN 1..(floor(random() * 10) + 1) LOOP
                        -- Выбираем случайного участника чата (кроме отправителя)
                        BEGIN
                            SELECT user_id INTO current_user_id
                            FROM messenger.chat_members
                            WHERE chat_id = current_chat_id
                              AND left_at IS NULL
                              AND user_id != current_sender_id
                            ORDER BY random()
                            LIMIT 1;

                            IF current_user_id IS NULL THEN
                                CONTINUE;
                            END IF;

                            -- Выбираем случайный эмодзи
                            random_emoji := reaction_emojis[floor(random() * emoji_count) + 1];

                            -- Добавляем реакцию
                            INSERT INTO messenger.message_reactions (
                                message_id,
                                message_created_at,
                                user_id,
                                emoji,
                                created_at
                            ) VALUES (
                                current_message_id,
                                current_created_at,
                                current_user_id,
                                random_emoji,
                                current_created_at + interval '1 minute' * floor(random() * 60 + 1)  -- реакция в течение часа после сообщения
                            );

                            reaction_count := reaction_count + 1;

                        EXCEPTION
                            WHEN unique_violation THEN
                                -- Эта реакция уже существует, пропускаем
                                CONTINUE;
                            WHEN OTHERS THEN
                                RAISE NOTICE 'Ошибка при добавлении реакции: %', SQLERRM;
                        END;
                    END LOOP;

                    -- Прогресс
                    IF k % 50 = 0 THEN
                        RAISE NOTICE '  Добавлено реакций: %', reaction_count;
                    END IF;
                END LOOP;

                RAISE NOTICE 'Создано % реакций', reaction_count;
            END;



    RAISE NOTICE '';
    RAISE NOTICE '=====================================';
    RAISE NOTICE 'ЗАПОЛНЕНИЕ БАЗЫ ЗАВЕРШЕНО';
    RAISE NOTICE '=====================================';

    -- Выводим статистику
    RAISE NOTICE '';
    RAISE NOTICE 'СТАТИСТИКА:';
    RAISE NOTICE 'Пользователи: %', (SELECT COUNT(*) FROM messenger.users);
    RAISE NOTICE 'Активные пользователи: %', (SELECT COUNT(*) FROM messenger.users WHERE deleted_at IS NULL);
    RAISE NOTICE 'Чаты: %', (SELECT COUNT(*) FROM messenger.chats);
    RAISE NOTICE '  • Приватные: %', (SELECT COUNT(*) FROM messenger.chats WHERE type = 'private');
    RAISE NOTICE '  • Группы: %', (SELECT COUNT(*) FROM messenger.chats WHERE type = 'group');
    RAISE NOTICE '  • Каналы: %', (SELECT COUNT(*) FROM messenger.chats WHERE type = 'channel');
    RAISE NOTICE 'Участники чатов: %', (SELECT COUNT(*) FROM messenger.chat_members);
    RAISE NOTICE 'Активные участники: %', (SELECT COUNT(*) FROM messenger.chat_members WHERE left_at IS NULL);
    RAISE NOTICE 'Сообщения: %', (SELECT COUNT(*) FROM messenger.messages);
    RAISE NOTICE 'Вложения: %', (SELECT COUNT(*) FROM messenger.attachments);
    RAISE NOTICE 'Реакции: %', (SELECT COUNT(*) FROM messenger.message_reactions);
    RAISE NOTICE '';
    RAISE NOTICE 'Распределение ролей:';
    RAISE NOTICE '  owner:  %', (SELECT COUNT(*) FROM messenger.chat_members WHERE role = 'owner');
    RAISE NOTICE '  admin:  %', (SELECT COUNT(*) FROM messenger.chat_members WHERE role = 'admin');
    RAISE NOTICE '  member: %', (SELECT COUNT(*) FROM messenger.chat_members WHERE role = 'member');
    RAISE NOTICE '  guest:  %', (SELECT COUNT(*) FROM messenger.chat_members WHERE role = 'guest');
    RAISE NOTICE '';
    RAISE NOTICE 'Для просмотра детальной статистики:';
    RAISE NOTICE '  SELECT * FROM messenger.v_chat_stats LIMIT 10;';
    RAISE NOTICE '  SELECT * FROM messenger.v_user_stats LIMIT 10;';
    RAISE NOTICE '  SELECT * FROM messenger.v_partitions;';

    RAISE NOTICE '';
    RAISE NOTICE 'Готово! База заполнена тестовыми данными.';

EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'Ошибка при заполнении базы: %', SQLERRM;
    RAISE NOTICE 'Код ошибки: %', SQLSTATE;
END $$;
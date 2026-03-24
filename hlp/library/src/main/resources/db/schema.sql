-- Домен для email (валидация формата)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'email_type') THEN
CREATE DOMAIN email_type AS VARCHAR(255) CHECK (value ~ '^[^@]+@[^@]+\.[^@]+$');
END IF;
END$$;

-- Домен для ISBN
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'isbn_type') THEN
CREATE DOMAIN isbn_type AS VARCHAR(20) CHECK (value ~ '^[0-9-]{10,20}$');
END IF;
END$$;

-- Книги (издания)
CREATE TABLE IF NOT EXISTS books (
                                     id   BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn isbn_type NOT NULL UNIQUE
    );

-- Статусы экземпляров
CREATE TABLE IF NOT EXISTS item_status (
                                           id   BIGSERIAL PRIMARY KEY,
                                           name VARCHAR(50) NOT NULL UNIQUE
    );

-- Начальные данные статусов
INSERT INTO item_status (name) VALUES ('AVAILABLE'), ('LOANED'), ('REPAIR'), ('LOST')
    ON CONFLICT (name) DO NOTHING;

-- Экземпляры книг
CREATE TABLE IF NOT EXISTS book_item (
    id               BIGSERIAL PRIMARY KEY,
    book_id          BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    status_id        BIGINT NOT NULL REFERENCES item_status(id),
    inventory_number VARCHAR(50) UNIQUE,
    comment          TEXT
);

-- Читатели
CREATE TABLE IF NOT EXISTS readers (
                                       id    BIGSERIAL PRIMARY KEY,
                                       name  VARCHAR(255) NOT NULL,
    email email_type   NOT NULL UNIQUE,
    phone VARCHAR(20)
    );

-- Выдачи
CREATE TABLE IF NOT EXISTS loans (
                                     id            BIGSERIAL PRIMARY KEY,
                                     book_item_id  BIGINT NOT NULL REFERENCES book_item(id) ON DELETE RESTRICT,
    reader_id     BIGINT NOT NULL REFERENCES readers(id) ON DELETE RESTRICT,
    borrow_date   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    return_date   TIMESTAMP,
    CONSTRAINT check_return_date CHECK (return_date IS NULL OR return_date >= borrow_date)
    );

-- Индексы
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_book_item_book_id ON book_item(book_id);
CREATE INDEX IF NOT EXISTS idx_book_item_status_id ON book_item(status_id);
CREATE INDEX IF NOT EXISTS idx_loans_book_item_id ON loans(book_item_id);
CREATE INDEX IF NOT EXISTS idx_loans_reader_id ON loans(reader_id);
CREATE INDEX IF NOT EXISTS idx_loans_return_date ON loans(return_date);
CREATE INDEX IF NOT EXISTS idx_readers_email ON readers(email);
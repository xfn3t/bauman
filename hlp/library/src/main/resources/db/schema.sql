-- Создание таблиц с проверкой существования
CREATE TABLE IF NOT EXISTS books (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    available BOOLEAN NOT NULL DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS readers (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20)
    );

CREATE TABLE IF NOT EXISTS loans (
                                     id BIGSERIAL PRIMARY KEY,
                                     book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE RESTRICT,
    reader_id BIGINT NOT NULL REFERENCES readers(id) ON DELETE RESTRICT,
    borrow_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    return_date TIMESTAMP,
    CONSTRAINT check_return_date CHECK (return_date IS NULL OR return_date >= borrow_date)
    );

-- Индексы (если не существуют)
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_loans_book_id ON loans(book_id);
CREATE INDEX IF NOT EXISTS idx_loans_reader_id ON loans(reader_id);
CREATE INDEX IF NOT EXISTS idx_loans_return_date ON loans(return_date);
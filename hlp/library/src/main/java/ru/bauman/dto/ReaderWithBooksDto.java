package ru.bauman.dto;

import java.util.ArrayList;
import java.util.List;

public record ReaderWithBooksDto(ReaderDto reader, List<BookDto> borrowedBooks) {

    public static class Builder {
        private final ReaderDto reader;
        private final List<BookDto> books = new ArrayList<>();

        public Builder(ReaderDto reader) {
            this.reader = reader;
        }

        public Builder addBook(BookDto book) {
            books.add(book);
            return this;
        }

        public ReaderWithBooksDto build() {
            return new ReaderWithBooksDto(reader, List.copyOf(books));
        }
    }
}
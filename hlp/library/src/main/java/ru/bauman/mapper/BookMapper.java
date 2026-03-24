package ru.bauman.mapper;

import ru.bauman.dto.BookDto;
import ru.bauman.model.Book;

public class BookMapper {

    public static BookDto toDto(Book book, int totalCopies, int availableCopies) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                totalCopies,
                availableCopies
        );
    }

    public static Book toEntity(BookDto dto) {
        return new Book(
                dto.id(),
                dto.title(),
                dto.author(),
                dto.isbn()
        );
    }
}
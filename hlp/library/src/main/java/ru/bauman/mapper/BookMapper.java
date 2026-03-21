package ru.bauman.mapper;

import java.util.List;

import ru.bauman.dto.BookDto;
import ru.bauman.model.Book;

public class BookMapper {

    public static BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.isAvailable()
        );
    }

    public static Book toEntity(BookDto dto) {
        return new Book(
                dto.id(),
                dto.title(),
                dto.author(),
                dto.isbn(),
                dto.available()
        );
    }

    public static List<BookDto> toDtoList(List<Book> books) {
        return books.stream().map(BookMapper::toDto).toList();
    }

    public static List<Book> toEntityList(List<BookDto> dtos) {
        return dtos.stream().map(BookMapper::toEntity).toList();
    }
}
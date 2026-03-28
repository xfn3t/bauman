package ru.bauman.dto;

import java.sql.Timestamp;

public record BookWithBorrowDateDto(
        Long bookId,
        String title,
        String author,
        String isbn,
        Timestamp borrowDate
) {}
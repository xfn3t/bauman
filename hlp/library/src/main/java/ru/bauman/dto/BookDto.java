package ru.bauman.dto;

public record BookDto(Long id, String title, String author, String isbn, int totalCopies, int availableCopies) {}
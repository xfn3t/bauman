package ru.bauman.tigerbank.category.dto;

public record CreateCategoryRequest(
        String name,
        CategoryTypeDto type
) {}
package ru.bauman.tigerbank.category.dto;

public record CategoryDto(
		Long id,
		String name,
		CategoryTypeDto type
) {}
package ru.bauman.tigerbank.category.service;

import ru.bauman.tigerbank.category.entity.CategoryType;
import ru.bauman.tigerbank.category.entity.CategoryTypeEnum;

public interface CategoryTypeEntityServiceInterface {
	CategoryType getById(Long id);
	CategoryType getByName(CategoryTypeEnum name);
}
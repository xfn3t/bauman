package ru.bauman.tigerbank.category.service.entity;

import ru.bauman.tigerbank.category.entity.CategoryType;
import ru.bauman.tigerbank.category.entity.CategoryTypeEnum;

public interface CategoryTypeEntityService {
	CategoryType getById(Long id);
	CategoryType getByName(CategoryTypeEnum name);
}
package ru.bauman.tigerbank.category.factory;

import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.dto.CreateCategoryRequest;

public interface CategoryFactory {
    CategoryDto create(CreateCategoryRequest request);
}
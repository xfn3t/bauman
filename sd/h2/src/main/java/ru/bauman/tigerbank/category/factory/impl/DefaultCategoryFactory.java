package ru.bauman.tigerbank.category.factory.impl;

import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.factory.CategoryFactory;
import ru.bauman.tigerbank.category.dto.CreateCategoryRequest;

@Component
public class DefaultCategoryFactory implements CategoryFactory {

    @Override
    public CategoryDto create(CreateCategoryRequest request) {
        return new CategoryDto(null, request.name(), request.type());
    }
}
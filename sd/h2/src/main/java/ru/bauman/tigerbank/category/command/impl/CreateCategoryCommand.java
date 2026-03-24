package ru.bauman.tigerbank.category.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.facade.CategoryFacade;
import ru.bauman.tigerbank.common.command.Command;
import ru.bauman.tigerbank.category.dto.CreateCategoryRequest;

@RequiredArgsConstructor
public class CreateCategoryCommand implements Command<CategoryDto> {

    private final CategoryFacade facade;
    private final CreateCategoryRequest request;

    @Override
    public CategoryDto execute() {
        return facade.create(request);
    }
}
package ru.bauman.tigerbank.category.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.facade.CategoryFacade;
import ru.bauman.tigerbank.common.command.Command;

@RequiredArgsConstructor
public class UpdateCategoryCommand implements Command<CategoryDto> {

    private final CategoryFacade facade;
    private final Long id;
    private final CategoryDto dto;

    @Override
    public CategoryDto execute() {
        return facade.update(id, dto);
    }
}
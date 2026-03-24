package ru.bauman.tigerbank.category.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.category.facade.CategoryFacade;
import ru.bauman.tigerbank.common.command.Command;

@RequiredArgsConstructor
public class DeleteCategoryCommand implements Command<Void> {

    private final CategoryFacade facade;
    private final Long id;

    @Override
    public Void execute() {
        facade.delete(id);
        return null;
    }
}
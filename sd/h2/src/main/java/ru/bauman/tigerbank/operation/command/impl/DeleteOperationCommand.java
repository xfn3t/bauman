package ru.bauman.tigerbank.operation.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.common.command.Command;
import ru.bauman.tigerbank.operation.facade.OperationFacade;

@RequiredArgsConstructor
public class DeleteOperationCommand implements Command<Void> {

    private final OperationFacade facade;
    private final Long id;

    @Override
    public Void execute() {
        facade.delete(id);
        return null;
    }
}
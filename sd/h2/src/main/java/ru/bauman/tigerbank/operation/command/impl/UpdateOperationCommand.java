package ru.bauman.tigerbank.operation.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.common.command.Command;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.facade.OperationFacade;

@RequiredArgsConstructor
public class UpdateOperationCommand implements Command<OperationDto> {

    private final OperationFacade facade;
    private final Long id;
    private final OperationDto dto;

    @Override
    public OperationDto execute() {
        return facade.update(id, dto);
    }
}
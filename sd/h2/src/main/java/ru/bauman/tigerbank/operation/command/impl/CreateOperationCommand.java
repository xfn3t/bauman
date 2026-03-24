package ru.bauman.tigerbank.operation.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.common.command.Command;
import ru.bauman.tigerbank.operation.dto.CreateOperationRequest;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.facade.OperationFacade;

@RequiredArgsConstructor
public class CreateOperationCommand implements Command<OperationDto> {

    private final OperationFacade facade;
    private final CreateOperationRequest request;

    @Override
    public OperationDto execute() {
        return facade.create(request);
    }
}
package ru.bauman.tigerbank.operation.factory;

import ru.bauman.tigerbank.operation.dto.CreateOperationRequest;
import ru.bauman.tigerbank.operation.dto.OperationDto;

public interface OperationFactory {
    OperationDto create(CreateOperationRequest request);
}
package ru.bauman.tigerbank.operation.factory.impl;

import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.operation.dto.CreateOperationRequest;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import java.time.LocalDateTime;
import ru.bauman.tigerbank.operation.factory.OperationFactory;

@Component
public class DefaultOperationFactory implements OperationFactory {

    @Override
    public OperationDto create(CreateOperationRequest request) {
        return new OperationDto(
                null,
                request.amount(),
                request.date() != null ? request.date() : LocalDateTime.now(),
                request.description(),
                request.type(),
                request.account(),
                request.category()
        );
    }
}
package ru.bauman.tigerbank.operation.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.operation.dto.CreateOperationRequest;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.factory.OperationFactory;
import ru.bauman.tigerbank.operation.service.OperationService;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OperationFacade {

    private final OperationService operationService;
    private final OperationFactory operationFactory;

    public OperationDto create(CreateOperationRequest request) {
        return operationService.create(operationFactory.create(request));
    }

    public OperationDto update(Long id, OperationDto dto) {
        return operationService.update(id, dto);
    }

    public void delete(Long id) {
        operationService.delete(id);
    }

    public OperationDto findById(Long id) {
        return operationService.findById(id);
    }

    public List<OperationDto> findAll() {
        return operationService.findAll();
    }

    public List<OperationDto> findByAccountAndPeriod(Long accountId, LocalDateTime from, LocalDateTime to) {
        return operationService.findByAccountAndPeriod(accountId, from, to);
    }
}
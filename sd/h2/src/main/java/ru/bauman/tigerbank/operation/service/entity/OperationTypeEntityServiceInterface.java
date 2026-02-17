package ru.bauman.tigerbank.operation.service.entity;

import ru.bauman.tigerbank.operation.entity.OperationType;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;

public interface OperationTypeEntityServiceInterface {
	OperationType getById(Long id);
	OperationType getByName(OperationTypeEnum name);
}
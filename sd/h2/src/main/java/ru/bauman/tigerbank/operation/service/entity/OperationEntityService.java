package ru.bauman.tigerbank.operation.service.entity;

import ru.bauman.tigerbank.operation.entity.Operation;
import java.time.LocalDateTime;
import java.util.List;

public interface OperationEntityService {
	Operation save(Operation operation);
	void deleteById(Long id);
	Operation getById(Long id);
	List<Operation> getAll();
	List<Operation> findByAccountAndPeriod(Long accountId, LocalDateTime from, LocalDateTime to);
	List<Operation> findByCategory(Long categoryId);
	List<Operation> findAllByAccountId(Long accountId);
}
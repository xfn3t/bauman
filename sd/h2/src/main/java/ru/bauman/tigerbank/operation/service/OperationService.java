package ru.bauman.tigerbank.operation.service;

import ru.bauman.tigerbank.operation.dto.OperationDto;
import java.time.LocalDateTime;
import java.util.List;

public interface OperationService {
	OperationDto create(OperationDto dto);
	OperationDto update(Long id, OperationDto dto);
	void delete(Long id);
	OperationDto findById(Long id);
	List<OperationDto> findAll();
	List<OperationDto> findByAccountAndPeriod(Long accountId, LocalDateTime from, LocalDateTime to);
}
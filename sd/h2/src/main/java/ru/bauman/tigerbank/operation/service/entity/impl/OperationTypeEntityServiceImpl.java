package ru.bauman.tigerbank.operation.service.entity.impl;

import ru.bauman.tigerbank.operation.entity.OperationType;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;
import ru.bauman.tigerbank.operation.repository.OperationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bauman.tigerbank.operation.service.entity.OperationTypeEntityService;

@Service
@RequiredArgsConstructor
public class OperationTypeEntityServiceImpl implements OperationTypeEntityService {
	private final OperationTypeRepository repository;

	@Override
	public OperationType getById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("OperationType not found with id: " + id));
	}

	@Override
	public OperationType getByName(OperationTypeEnum name) {
		return repository.findByName(name)
				.orElseThrow(() -> new RuntimeException("OperationType not found with name: " + name));
	}
}
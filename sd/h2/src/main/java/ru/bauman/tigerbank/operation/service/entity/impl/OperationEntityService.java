package ru.bauman.tigerbank.operation.service.entity.impl;

import ru.bauman.tigerbank.operation.entity.Operation;
import ru.bauman.tigerbank.operation.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.tigerbank.operation.service.entity.OperationEntityServiceInterface;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationEntityService implements OperationEntityServiceInterface {
	private final OperationRepository repository;

	@Override
	@Transactional
	public Operation save(Operation operation) {
		return repository.save(operation);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	@Override
	public Operation getById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Operation not found with id: " + id));
	}

	@Override
	public List<Operation> getAll() {
		return repository.findAll();
	}

	@Override
	public List<Operation> findByAccountAndPeriod(Long accountId, LocalDateTime from, LocalDateTime to) {
		if (from == null || to == null) {
			return repository.findByAccountIdAndDateBetween(accountId, LocalDateTime.MIN, LocalDateTime.MAX);
		}
		return repository.findByAccountIdAndDateBetween(accountId, from, to);
	}

	@Override
	public List<Operation> findByCategory(Long categoryId) {
		return repository.findByCategoryId(categoryId);
	}
}
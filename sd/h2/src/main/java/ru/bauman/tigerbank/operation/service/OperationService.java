package ru.bauman.tigerbank.operation.service;

import ru.bauman.tigerbank.account.service.entity.BankAccountEntityServiceInterface;
import ru.bauman.tigerbank.category.service.CategoryEntityServiceInterface;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.mapper.OperationMapper;
import ru.bauman.tigerbank.operation.entity.Operation;
import ru.bauman.tigerbank.operation.service.entity.OperationEntityServiceInterface;
import ru.bauman.tigerbank.operation.service.entity.OperationTypeEntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService implements OperationServiceInterface {
	private final OperationEntityServiceInterface entityService;
	private final BankAccountEntityServiceInterface accountEntityService;
	private final CategoryEntityServiceInterface categoryEntityService;
	private final OperationTypeEntityServiceInterface operationTypeEntityService;
	private final OperationMapper mapper;

	@Override
	@Transactional
	public OperationDto create(OperationDto dto) {
		Operation entity = mapper.toEntity(dto);
		entity.setId(null);
		if (dto.account() != null && dto.account().id() != null) {
			entity.setAccount(accountEntityService.getById(dto.account().id()));
		} else {
			throw new RuntimeException("Account is required");
		}
		if (dto.category() != null && dto.category().id() != null) {
			entity.setCategory(categoryEntityService.getById(dto.category().id()));
		} else {
			throw new RuntimeException("Category is required");
		}
		if (dto.type() != null && dto.type().id() != null) {
			entity.setType(operationTypeEntityService.getById(dto.type().id()));
		} else {
			throw new RuntimeException("Operation type is required");
		}
		if (entity.getDate() == null) {
			entity.setDate(LocalDateTime.now());
		}
		Operation saved = entityService.save(entity);
		return mapper.toDto(saved);
	}

	@Override
	@Transactional
	public OperationDto update(Long id, OperationDto dto) {
		Operation existing = entityService.getById(id);
		existing.setAmount(dto.amount());
		existing.setDate(dto.date());
		existing.setDescription(dto.description());
		if (dto.type() != null && dto.type().id() != null) {
			existing.setType(operationTypeEntityService.getById(dto.type().id()));
		}
		if (dto.account() != null && dto.account().id() != null) {
			existing.setAccount(accountEntityService.getById(dto.account().id()));
		}
		if (dto.category() != null && dto.category().id() != null) {
			existing.setCategory(categoryEntityService.getById(dto.category().id()));
		}
		return mapper.toDto(entityService.save(existing));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		entityService.deleteById(id);
	}

	@Override
	public OperationDto findById(Long id) {
		return mapper.toDto(entityService.getById(id));
	}

	@Override
	public List<OperationDto> findAll() {
		return mapper.toDtoList(entityService.getAll());
	}

	@Override
	public List<OperationDto> findByAccountAndPeriod(Long accountId, LocalDateTime from, LocalDateTime to) {
		return mapper.toDtoList(entityService.findByAccountAndPeriod(accountId, from, to));
	}
}
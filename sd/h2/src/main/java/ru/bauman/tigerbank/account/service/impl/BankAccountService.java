package ru.bauman.tigerbank.account.service.impl;

import ru.bauman.tigerbank.account.service.BankAccountServiceInterface;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.mapper.BankAccountMapper;
import ru.bauman.tigerbank.account.entity.BankAccount;
import ru.bauman.tigerbank.account.service.entity.BankAccountEntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountService implements BankAccountServiceInterface {
	private final BankAccountEntityServiceInterface entityService;
	private final BankAccountMapper mapper;

	@Override
	@Transactional
	public BankAccountDto create(BankAccountDto dto) {
		BankAccount entity = mapper.toEntity(dto);
		entity.setId(null);
		BankAccount saved = entityService.save(entity);
		return mapper.toDto(saved);
	}

	@Override
	@Transactional
	public BankAccountDto update(Long id, BankAccountDto dto) {
		BankAccount existing = entityService.getById(id);
		existing.setName(dto.name());
		existing.setBalance(dto.balance());
		return mapper.toDto(entityService.save(existing));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		entityService.deleteById(id);
	}

	@Override
	public BankAccountDto findById(Long id) {
		return mapper.toDto(entityService.getById(id));
	}

	@Override
	public List<BankAccountDto> findAll() {
		return mapper.toDtoList(entityService.getAll());
	}
}
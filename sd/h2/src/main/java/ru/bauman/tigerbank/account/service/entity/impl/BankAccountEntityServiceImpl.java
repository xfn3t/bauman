package ru.bauman.tigerbank.account.service.entity.impl;

import ru.bauman.tigerbank.account.entity.BankAccount;
import ru.bauman.tigerbank.account.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.tigerbank.account.service.entity.BankAccountEntityService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountEntityServiceImpl implements BankAccountEntityService {

	private final BankAccountRepository repository;

	@Override
	@Transactional
	public BankAccount save(BankAccount account) {
		return repository.save(account);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	@Override
	public BankAccount getById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("BankAccount not found with id: " + id));
	}

	@Override
	public List<BankAccount> getAll() {
		return repository.findAll();
	}
}
package ru.bauman.tigerbank.account.service;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import java.util.List;

public interface BankAccountService {
	BankAccountDto create(BankAccountDto dto);
	BankAccountDto update(Long id, BankAccountDto dto);
	void delete(Long id);
	BankAccountDto findById(Long id);
	List<BankAccountDto> findAll();
}
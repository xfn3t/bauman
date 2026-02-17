package ru.bauman.tigerbank.account.service.entity;

import ru.bauman.tigerbank.account.entity.BankAccount;
import java.util.List;

public interface BankAccountEntityService {
	BankAccount save(BankAccount account);
	void deleteById(Long id);
	BankAccount getById(Long id);
	List<BankAccount> getAll();
}
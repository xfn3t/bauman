package ru.bauman.tigerbank.account.service.balance.impl;

import ru.bauman.tigerbank.account.entity.BankAccount;
import ru.bauman.tigerbank.account.service.balance.BalanceCalculator;
import ru.bauman.tigerbank.account.service.balance.BalanceRecalculationService;
import ru.bauman.tigerbank.operation.entity.Operation;
import ru.bauman.tigerbank.account.service.entity.BankAccountEntityService;
import ru.bauman.tigerbank.operation.service.entity.OperationEntityService;
import ru.bauman.tigerbank.operation.service.entity.OperationTypeEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceRecalculationServiceImpl implements BalanceRecalculationService {

	private final BankAccountEntityService accountEntityService;
	private final OperationEntityService operationEntityService;
	private final BalanceCalculator balanceCalculator;

	@Override
	@Transactional
	public void autoRecalc(Long accountId) {
		BankAccount account = accountEntityService.getById(accountId);
		List<Operation> operations = operationEntityService.findAllByAccountId(accountId);
		BigDecimal balance = balanceCalculator.calculate(operations);
		account.setBalance(balance);
		accountEntityService.save(account);
	}

	@Override
	@Transactional
	public void manualRecalc(Long accountId, BigDecimal correctBalance) {
		autoRecalc(accountId);
		BankAccount account = accountEntityService.getById(accountId);
		if (account.getBalance().compareTo(correctBalance) != 0) { // validate balance
			account.setBalance(correctBalance);
			accountEntityService.save(account);
		}
	}
}
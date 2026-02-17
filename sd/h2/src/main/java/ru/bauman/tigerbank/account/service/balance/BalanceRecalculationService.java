package ru.bauman.tigerbank.account.service.balance;

import ru.bauman.tigerbank.account.entity.BankAccount;
import ru.bauman.tigerbank.operation.entity.Operation;
import ru.bauman.tigerbank.operation.entity.OperationType;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;
import ru.bauman.tigerbank.account.service.entity.BankAccountEntityServiceInterface;
import ru.bauman.tigerbank.operation.service.entity.OperationEntityServiceInterface;
import ru.bauman.tigerbank.operation.service.entity.OperationTypeEntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceRecalculationService implements BalanceRecalculationServiceInterface {
	private final BankAccountEntityServiceInterface accountEntityService;
	private final OperationEntityServiceInterface operationEntityService;
	private final OperationTypeEntityServiceInterface operationTypeEntityService;

	@Override
	@Transactional
	public void autoRecalc(Long accountId) {
		BankAccount account = accountEntityService.getById(accountId);
		OperationType incomeType = operationTypeEntityService.getByName(OperationTypeEnum.INCOME);
		OperationType expenseType = operationTypeEntityService.getByName(OperationTypeEnum.EXPENSE);
		List<Operation> operations = operationEntityService.findByAccountAndPeriod(accountId, null, null);
		BigDecimal balance = operations.stream()
				.map(op -> {
					if (op.getType().getId().equals(incomeType.getId())) {
						return op.getAmount();
					} else if (op.getType().getId().equals(expenseType.getId())) {
						return op.getAmount().negate();
					} else {
						return BigDecimal.ZERO;
					}
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		account.setBalance(balance);
		accountEntityService.save(account);
	}

	@Override
	@Transactional
	public void manualRecalc(Long accountId, BigDecimal correctBalance) {
		autoRecalc(accountId);
		BankAccount account = accountEntityService.getById(accountId);
		if (account.getBalance().compareTo(correctBalance) != 0) {
			account.setBalance(correctBalance);
			accountEntityService.save(account);
		}
	}
}
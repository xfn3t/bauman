package ru.bauman.tigerbank.account.service.balance.impl;

import ru.bauman.tigerbank.account.service.balance.BalanceCalculator;
import ru.bauman.tigerbank.operation.entity.Operation;

import java.math.BigDecimal;
import java.util.List;

public class DefaultBalanceCalculator implements BalanceCalculator {
	@Override
	public BigDecimal calculate(List<Operation> operations) {
		return operations.stream()
				.map(op -> op.getType().getName().applyEffect(op.getAmount()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}

package ru.bauman.tigerbank.account.service.balance;

import ru.bauman.tigerbank.operation.entity.Operation;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceCalculator {
	BigDecimal calculate(List<Operation> operations);
}
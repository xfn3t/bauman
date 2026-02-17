package ru.bauman.tigerbank.account.service.balance;

import java.math.BigDecimal;

public interface BalanceRecalculationService {
	void autoRecalc(Long accountId);
	void manualRecalc(Long accountId, BigDecimal correctBalance);
}
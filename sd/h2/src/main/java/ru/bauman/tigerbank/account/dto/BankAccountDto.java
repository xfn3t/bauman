package ru.bauman.tigerbank.account.dto;

import java.math.BigDecimal;

public record BankAccountDto(
		Long id,
		String name,
		BigDecimal balance
) {}
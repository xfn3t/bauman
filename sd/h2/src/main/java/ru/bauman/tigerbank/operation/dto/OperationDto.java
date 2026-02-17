package ru.bauman.tigerbank.operation.dto;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperationDto(
		Long id,
		BigDecimal amount,
		LocalDateTime date,
		String description,
		OperationTypeDto type,
		BankAccountDto account,
		CategoryDto category
) {}
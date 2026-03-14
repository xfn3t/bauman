package ru.bauman.tigerbank.importing.dto;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.operation.dto.OperationDto;

import java.util.List;

public record ImportData(
		List<BankAccountDto> accounts,
		List<CategoryDto> categories,
		List<OperationDto> operations
) {}
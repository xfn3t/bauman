package ru.bauman.tigerbank.operation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;

public record CreateOperationRequest(
        BigDecimal amount,
        LocalDateTime date,
        String description,
        OperationTypeDto type,
        BankAccountDto account,
        CategoryDto category
) {}
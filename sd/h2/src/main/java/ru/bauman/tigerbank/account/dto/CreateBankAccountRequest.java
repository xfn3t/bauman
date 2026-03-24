package ru.bauman.tigerbank.account.dto;

import java.math.BigDecimal;

public record CreateBankAccountRequest(
        String name,
        BigDecimal balance
) {}
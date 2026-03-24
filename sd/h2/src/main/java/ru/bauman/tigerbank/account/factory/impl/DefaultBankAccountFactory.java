package ru.bauman.tigerbank.account.factory.impl;

import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.factory.BankAccountFactory;
import ru.bauman.tigerbank.account.dto.CreateBankAccountRequest;
import java.math.BigDecimal;

@Component
public class DefaultBankAccountFactory implements BankAccountFactory {

    @Override
    public BankAccountDto create(CreateBankAccountRequest request) {
        return new BankAccountDto(
                null,
                request.name(),
                request.balance() != null ? request.balance() : BigDecimal.ZERO
        );
    }
}
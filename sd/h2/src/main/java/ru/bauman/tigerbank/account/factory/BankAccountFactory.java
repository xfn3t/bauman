package ru.bauman.tigerbank.account.factory;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.dto.CreateBankAccountRequest;

public interface BankAccountFactory {
    BankAccountDto create(CreateBankAccountRequest request);
}
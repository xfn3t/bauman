package ru.bauman.tigerbank.account.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.facade.BankAccountFacade;
import ru.bauman.tigerbank.common.command.Command;
import ru.bauman.tigerbank.account.dto.CreateBankAccountRequest;

@RequiredArgsConstructor
public class CreateBankAccountCommand implements Command<BankAccountDto> {

    private final BankAccountFacade facade;
    private final CreateBankAccountRequest request;

    @Override
    public BankAccountDto execute() {
        return facade.create(request);
    }
}
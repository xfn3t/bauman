package ru.bauman.tigerbank.account.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.facade.BankAccountFacade;
import ru.bauman.tigerbank.common.command.Command;

@RequiredArgsConstructor
public class UpdateBankAccountCommand implements Command<BankAccountDto> {

    private final BankAccountFacade facade;
    private final Long id;
    private final BankAccountDto dto;

    @Override
    public BankAccountDto execute() {
        return facade.update(id, dto);
    }
}
package ru.bauman.tigerbank.account.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.account.facade.BankAccountFacade;
import ru.bauman.tigerbank.common.command.Command;

@RequiredArgsConstructor
public class DeleteBankAccountCommand implements Command<Void> {

    private final BankAccountFacade facade;
    private final Long id;

    @Override
    public Void execute() {
        facade.delete(id);
        return null;
    }
}
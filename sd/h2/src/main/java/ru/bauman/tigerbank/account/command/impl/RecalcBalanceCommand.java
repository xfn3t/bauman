package ru.bauman.tigerbank.account.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.account.facade.BankAccountFacade;
import ru.bauman.tigerbank.common.command.Command;

@RequiredArgsConstructor
public class RecalcBalanceCommand implements Command<Void> {

    private final BankAccountFacade facade;
    private final Long accountId;

    @Override
    public Void execute() {
        facade.autoRecalcBalance(accountId);
        return null;
    }
}
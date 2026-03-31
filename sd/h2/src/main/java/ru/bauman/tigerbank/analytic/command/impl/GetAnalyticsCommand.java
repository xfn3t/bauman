package ru.bauman.tigerbank.analytic.command.impl;

import lombok.RequiredArgsConstructor;
import ru.bauman.tigerbank.analytic.facade.AnalyticsFacade;
import ru.bauman.tigerbank.common.command.Command;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class GetAnalyticsCommand implements Command<BigDecimal> {

    private final AnalyticsFacade facade;
    private final Long accountId;
    private final LocalDateTime from;
    private final LocalDateTime to;

    @Override
    public BigDecimal execute() {
        return facade.getIncomeExpenseDifference(accountId, from, to);
    }
}
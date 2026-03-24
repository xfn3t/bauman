package ru.bauman.tigerbank.analytic.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.analytic.AnalyticsService;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AnalyticsFacade {

    private final AnalyticsService analyticsService;

    public BigDecimal getIncomeExpenseDifference(Long accountId, LocalDateTime from, LocalDateTime to) {
        return analyticsService.getIncomeExpenseDifference(accountId, from, to);
    }

    public Map<CategoryDto, BigDecimal> groupIncomeByCategory(Long accountId, LocalDateTime from, LocalDateTime to) {
        return analyticsService.groupByCategory(accountId, from, to, OperationTypeEnum.INCOME);
    }

    public Map<CategoryDto, BigDecimal> groupExpenseByCategory(Long accountId, LocalDateTime from, LocalDateTime to) {
        return analyticsService.groupByCategory(accountId, from, to, OperationTypeEnum.EXPENSE);
    }
}
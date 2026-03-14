package ru.bauman.tigerbank.analytic;

import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface AnalyticsService {
	BigDecimal getIncomeExpenseDifference(Long accountId, LocalDateTime from, LocalDateTime to);
	Map<CategoryDto, BigDecimal> groupByCategory(Long accountId, LocalDateTime from, LocalDateTime to, OperationTypeEnum typeEnum);
}
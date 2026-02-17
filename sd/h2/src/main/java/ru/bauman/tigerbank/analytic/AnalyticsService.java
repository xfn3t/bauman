package ru.bauman.tigerbank.analytic;

import ru.bauman.tigerbank.category.entity.Category;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.mapper.CategoryMapper;
import ru.bauman.tigerbank.operation.entity.Operation;
import ru.bauman.tigerbank.operation.entity.OperationType;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;
import ru.bauman.tigerbank.operation.service.entity.OperationEntityServiceInterface;
import ru.bauman.tigerbank.operation.service.entity.OperationTypeEntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService implements AnalyticsServiceInterface {
	private final OperationEntityServiceInterface operationEntityService;
	private final OperationTypeEntityServiceInterface operationTypeEntityService;
	private final CategoryMapper categoryMapper;

	@Override
	public BigDecimal getIncomeExpenseDifference(Long accountId, LocalDateTime from, LocalDateTime to) {
		OperationType incomeType = operationTypeEntityService.getByName(OperationTypeEnum.INCOME);
		OperationType expenseType = operationTypeEntityService.getByName(OperationTypeEnum.EXPENSE);
		List<Operation> operations = operationEntityService.findByAccountAndPeriod(accountId, from, to);
		BigDecimal income = operations.stream()
				.filter(op -> op.getType().getId().equals(incomeType.getId()))
				.map(Operation::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal expense = operations.stream()
				.filter(op -> op.getType().getId().equals(expenseType.getId()))
				.map(Operation::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return income.subtract(expense);
	}

	@Override
	public Map<CategoryDto, BigDecimal> groupByCategory(Long accountId, LocalDateTime from, LocalDateTime to, OperationTypeEnum typeEnum) {
		OperationType type = operationTypeEntityService.getByName(typeEnum);
		List<Operation> operations = operationEntityService.findByAccountAndPeriod(accountId, from, to).stream()
				.filter(op -> op.getType().getId().equals(type.getId()))
				.toList();
		Map<Category, BigDecimal> map = operations.stream()
				.collect(Collectors.groupingBy(
						Operation::getCategory,
						Collectors.reducing(BigDecimal.ZERO, Operation::getAmount, BigDecimal::add)
				));
		return map.entrySet().stream()
				.collect(Collectors.toMap(
						e -> categoryMapper.toDto(e.getKey()),
						Map.Entry::getValue
				));
	}
}
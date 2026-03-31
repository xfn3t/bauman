package ru.bauman.tigerbank.analytic.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.dto.CategoryTypeDto;
import ru.bauman.tigerbank.category.entity.Category;
import ru.bauman.tigerbank.category.mapper.CategoryMapper;
import ru.bauman.tigerbank.operation.entity.Operation;
import ru.bauman.tigerbank.operation.entity.OperationType;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;
import ru.bauman.tigerbank.operation.service.entity.OperationEntityService;
import ru.bauman.tigerbank.operation.service.entity.OperationTypeEntityService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

	@Mock
	private OperationEntityService operationEntityService;

	@Mock
	private OperationTypeEntityService operationTypeEntityService;

	@Mock
	private CategoryMapper categoryMapper;

	@InjectMocks
	private AnalyticsServiceImpl analyticsService;

	@Test
	void getIncomeExpenseDifference_ShouldCalculateCorrectly() {
		Long accountId = 1L;
		LocalDateTime from = LocalDateTime.now().minusDays(10);
		LocalDateTime to = LocalDateTime.now();

		OperationType incomeType = OperationType.builder().id(1L).name(OperationTypeEnum.INCOME).build();
		OperationType expenseType = OperationType.builder().id(2L).name(OperationTypeEnum.EXPENSE).build();

		Operation incomeOp1 = Operation.builder().amount(BigDecimal.valueOf(1000)).type(incomeType).build();
		Operation incomeOp2 = Operation.builder().amount(BigDecimal.valueOf(500)).type(incomeType).build();
		Operation expenseOp1 = Operation.builder().amount(BigDecimal.valueOf(300)).type(expenseType).build();
		Operation expenseOp2 = Operation.builder().amount(BigDecimal.valueOf(200)).type(expenseType).build();

		List<Operation> operations = List.of(incomeOp1, incomeOp2, expenseOp1, expenseOp2);

		when(operationTypeEntityService.getByName(OperationTypeEnum.INCOME)).thenReturn(incomeType);
		when(operationTypeEntityService.getByName(OperationTypeEnum.EXPENSE)).thenReturn(expenseType);
		when(operationEntityService.findByAccountAndPeriod(accountId, from, to)).thenReturn(operations);

		BigDecimal difference = analyticsService.getIncomeExpenseDifference(accountId, from, to);

		assertThat(difference).isEqualByComparingTo("1000");
	}

	@Test
	void groupByCategory_ShouldReturnMapWithDtoKeys() {
		Long accountId = 1L;
		LocalDateTime from = LocalDateTime.now().minusDays(10);
		LocalDateTime to = LocalDateTime.now();
		OperationTypeEnum typeEnum = OperationTypeEnum.EXPENSE;

		OperationType expenseType = OperationType.builder().id(2L).name(OperationTypeEnum.EXPENSE).build();
		Category foodCategory = Category.builder().id(10L).name("Еда").build();
		Category transportCategory = Category.builder().id(11L).name("Транспорт").build();

		Operation op1 = Operation.builder().amount(BigDecimal.valueOf(500)).type(expenseType).category(foodCategory).build();
		Operation op2 = Operation.builder().amount(BigDecimal.valueOf(300)).type(expenseType).category(foodCategory).build();
		Operation op3 = Operation.builder().amount(BigDecimal.valueOf(200)).type(expenseType).category(transportCategory).build();

		List<Operation> operations = List.of(op1, op2, op3);

		CategoryDto foodDto = new CategoryDto(10L, "Еда", new CategoryTypeDto(2L, "EXPENSE"));
		CategoryDto transportDto = new CategoryDto(11L, "Транспорт", new CategoryTypeDto(2L, "EXPENSE"));

		when(operationTypeEntityService.getByName(typeEnum)).thenReturn(expenseType);
		when(operationEntityService.findByAccountAndPeriod(accountId, from, to)).thenReturn(operations);
		when(categoryMapper.toDto(foodCategory)).thenReturn(foodDto);
		when(categoryMapper.toDto(transportCategory)).thenReturn(transportDto);

		Map<CategoryDto, BigDecimal> result = analyticsService.groupByCategory(accountId, from, to, typeEnum);

		assertThat(result).hasSize(2);
		assertThat(result.get(foodDto)).isEqualByComparingTo("800");
		assertThat(result.get(transportDto)).isEqualByComparingTo("200");
	}
}
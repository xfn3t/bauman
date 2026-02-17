package ru.bauman.tigerbank.common.console;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.dto.CategoryTypeDto;
import ru.bauman.tigerbank.common.config.Measured;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.dto.OperationTypeDto;
import ru.bauman.tigerbank.account.service.BankAccountServiceInterface;
import ru.bauman.tigerbank.category.service.CategoryServiceInterface;
import ru.bauman.tigerbank.operation.service.OperationServiceInterface;
import ru.bauman.tigerbank.analytic.AnalyticsServiceInterface;
import ru.bauman.tigerbank.account.service.balance.BalanceRecalculationServiceInterface;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.export.impl.ExportService;
import ru.bauman.tigerbank.importing.ImportService;
import ru.bauman.tigerbank.common.config.statistic.StatisticServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemoRunner implements CommandLineRunner {

	private final BankAccountServiceInterface accountService;
	private final CategoryServiceInterface categoryService;
	private final OperationServiceInterface operationService;
	private final AnalyticsServiceInterface analyticsService;
	private final ExportService exportService;
	private final ImportService importService;
	private final BalanceRecalculationServiceInterface balanceService;
	private final StatisticServiceInterface statisticService;

	@Override
	@Measured
	public void run(String... args) throws Exception {
		log.info("=== Демонстрация работы модуля «Учет финансов» ТигрБанка ===");

		CategoryTypeDto incomeCatType = new CategoryTypeDto(1L, "INCOME");
		CategoryTypeDto expenseCatType = new CategoryTypeDto(2L, "EXPENSE");
		OperationTypeDto incomeOpType = new OperationTypeDto(1L, "INCOME");
		OperationTypeDto expenseOpType = new OperationTypeDto(2L, "EXPENSE");

		BankAccountDto account = accountService.create(new BankAccountDto(null, "Основной счет", BigDecimal.ZERO));
		log.info("Создан счет: {}", account);

		CategoryDto foodCategory = categoryService.create(new CategoryDto(null, "Еда", expenseCatType));
		CategoryDto salaryCategory = categoryService.create(new CategoryDto(null, "Зарплата", incomeCatType));
		log.info("Созданы категории: food={}, salary={}", foodCategory, salaryCategory);

		OperationDto incomeOp = operationService.create(new OperationDto(
				null,
				BigDecimal.valueOf(100000),
				LocalDateTime.now().minusDays(5),
				"Зарплата за февраль",
				incomeOpType,
				account,
				salaryCategory
		));
		OperationDto expenseOp = operationService.create(new OperationDto(
				null,
				BigDecimal.valueOf(3500),
				LocalDateTime.now().minusDays(2),
				"Ужин в ресторане",
				expenseOpType,
				account,
				foodCategory
		));
		log.info("Добавлены операции: доход={}, расход={}", incomeOp, expenseOp);

		LocalDateTime from = LocalDateTime.now().minusMonths(1).with(LocalTime.MIN);
		LocalDateTime to = LocalDateTime.now().with(LocalTime.MAX);
		BigDecimal diff = analyticsService.getIncomeExpenseDifference(account.id(), from, to);
		log.info("Разница доходов/расходов за последний месяц: {}", diff);

		Map<CategoryDto, BigDecimal> groupedIncome = analyticsService.groupByCategory(account.id(), from, to, OperationTypeEnum.INCOME);
		log.info("Доходы по категориям: {}", groupedIncome);
		Map<CategoryDto, BigDecimal> groupedExpense = analyticsService.groupByCategory(account.id(), from, to, OperationTypeEnum.EXPENSE);
		log.info("Расходы по категориям: {}", groupedExpense);

		exportService.exportData(ExportFormat.JSON, "export.json");
		exportService.exportData(ExportFormat.CSV, "export.csv");
		exportService.exportData(ExportFormat.YAML, "export.yaml");
		exportService.exportData(ExportFormat.PDF, "export.pdf");
		log.info("Экспорт завершён в файлы export.*");

		try {
			importService.importData(ExportFormat.CSV, "export.csv");
			log.info("Импорт из CSV выполнен");
		} catch (Exception e) {
			log.error("Импорт не удался: {}", e.getMessage());
		}

		balanceService.autoRecalc(account.id());
		BankAccountDto updatedAccount = accountService.findById(account.id());
		log.info("Баланс после авто-пересчёта: {}", updatedAccount.balance());

		statisticService.printStatistics();

		log.info("=== Демонстрация завершена ===");
	}
}
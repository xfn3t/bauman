package ru.bauman.tigerbank.common.console;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.account.command.impl.CreateBankAccountCommand;
import ru.bauman.tigerbank.account.command.impl.RecalcBalanceCommand;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.facade.BankAccountFacade;
import ru.bauman.tigerbank.analytic.command.impl.GetAnalyticsCommand;
import ru.bauman.tigerbank.analytic.facade.AnalyticsFacade;
import ru.bauman.tigerbank.category.command.impl.CreateCategoryCommand;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.dto.CategoryTypeDto;
import ru.bauman.tigerbank.category.facade.CategoryFacade;
import ru.bauman.tigerbank.common.command.CommandInvoker;
import ru.bauman.tigerbank.common.config.Measured;
import ru.bauman.tigerbank.common.config.statistic.StatisticServiceInterface;
import ru.bauman.tigerbank.export.ExportFormat;
import ru.bauman.tigerbank.export.ExportService;
import ru.bauman.tigerbank.export.command.impl.ExportDataCommand;
import ru.bauman.tigerbank.account.dto.CreateBankAccountRequest;
import ru.bauman.tigerbank.category.dto.CreateCategoryRequest;
import ru.bauman.tigerbank.importing.command.impl.ImportDataCommand;
import ru.bauman.tigerbank.operation.command.impl.CreateOperationCommand;
import ru.bauman.tigerbank.operation.dto.CreateOperationRequest;
import ru.bauman.tigerbank.importing.template.AbstractImportHandler;
import ru.bauman.tigerbank.operation.dto.OperationTypeDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import ru.bauman.tigerbank.operation.facade.OperationFacade;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DemoRunner implements CommandLineRunner {

	private final BankAccountFacade accountFacade;
	private final CategoryFacade categoryFacade;
	private final OperationFacade operationFacade;
	private final AnalyticsFacade analyticsFacade;
	private final CommandInvoker invoker;
	private final ExportService exportService;
	private final List<AbstractImportHandler> importHandlers;
	private final StatisticServiceInterface statisticService;

	@Override
	@Measured
	public void run(String... args) {
		log.info("=== Демонстрация ТигрБанк ===");

		CategoryTypeDto incomeType  = new CategoryTypeDto(1L, "INCOME");
		CategoryTypeDto expenseType = new CategoryTypeDto(2L, "EXPENSE");
		OperationTypeDto incomeOpType  = new OperationTypeDto(1L, "INCOME");
		OperationTypeDto expenseOpType = new OperationTypeDto(2L, "EXPENSE");

		BankAccountDto account = invoker.invoke(
				new CreateBankAccountCommand(accountFacade,
						new CreateBankAccountRequest("Основной счёт", BigDecimal.ZERO)));
		log.info("Создан счёт: {}", account);

		CategoryDto foodCat = invoker.invoke(
				new CreateCategoryCommand(categoryFacade, new CreateCategoryRequest("Еда", expenseType)));
		CategoryDto salCat = invoker.invoke(
				new CreateCategoryCommand(categoryFacade, new CreateCategoryRequest("Зарплата", incomeType)));
		log.info("Категории: food={}, salary={}", foodCat, salCat);

		invoker.invoke(new CreateOperationCommand(operationFacade,
				new CreateOperationRequest(BigDecimal.valueOf(100_000),
						LocalDateTime.now().minusDays(5), "Зарплата за февраль",
						incomeOpType, account, salCat)));

		invoker.invoke(new CreateOperationCommand(operationFacade,
				new CreateOperationRequest(BigDecimal.valueOf(3_500),
						LocalDateTime.now().minusDays(2), "Ужин в ресторане",
						expenseOpType, account, foodCat)));

		log.info("Операции добавлены");

		LocalDateTime from = LocalDateTime.now().minusMonths(1).with(LocalTime.MIN);
		LocalDateTime to   = LocalDateTime.now().with(LocalTime.MAX);

		BigDecimal diff = invoker.invoke(new GetAnalyticsCommand(analyticsFacade, account.id(), from, to));
		log.info("Разница доходов/расходов: {}", diff);
		log.info("Доходы по категориям: {}",  analyticsFacade.groupIncomeByCategory(account.id(), from, to));
		log.info("Расходы по категориям: {}", analyticsFacade.groupExpenseByCategory(account.id(), from, to));

		for (ExportFormat fmt : ExportFormat.values()) {
			invoker.invoke(new ExportDataCommand(exportService, fmt, "export." + fmt.name().toLowerCase()));
		}
		log.info("Экспорт завершён");

		Map<ExportFormat, AbstractImportHandler> handlerMap = importHandlers.stream()
				.collect(Collectors.toMap(AbstractImportHandler::getFormat, Function.identity()));

		invoker.invoke(new ImportDataCommand(handlerMap, ExportFormat.CSV, "export.csv"));
		log.info("Импорт из CSV выполнен");

		invoker.invoke(new RecalcBalanceCommand(accountFacade, account.id()));
		log.info("Баланс после пересчёта: {}", accountFacade.findById(account.id()).balance());

		statisticService.printStatistics();
		log.info("=== Демонстрация завершена ===");
	}
}
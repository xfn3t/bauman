package ru.bauman.tigerbank.export;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.service.BankAccountService;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.dto.CategoryTypeDto;
import ru.bauman.tigerbank.category.service.CategoryService;
import ru.bauman.tigerbank.importing.ImportService;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.dto.OperationTypeDto;
import ru.bauman.tigerbank.operation.service.OperationService;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ExportImportIntegrationTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private ExportService exportService;

	@Autowired
	private ImportService importService;

	@Autowired
	private BankAccountService accountService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private OperationService operationService;

	@Test
	void shouldExportAndImportCsv() throws Exception {

		CategoryTypeDto expenseCatType = new CategoryTypeDto(2L, "EXPENSE");
		CategoryTypeDto incomeCatType = new CategoryTypeDto(1L, "INCOME");
		OperationTypeDto incomeOpType = new OperationTypeDto(1L, "INCOME");
		OperationTypeDto expenseOpType = new OperationTypeDto(2L, "EXPENSE");

		BankAccountDto account = accountService.create(new BankAccountDto(null, "Экспорт счет", BigDecimal.valueOf(1000)));
		CategoryDto food = categoryService.create(new CategoryDto(null, "Еда", expenseCatType));
		CategoryDto salary = categoryService.create(new CategoryDto(null, "Зарплата", incomeCatType));
		OperationDto op1 = operationService.create(new OperationDto(
				null, BigDecimal.valueOf(500), LocalDateTime.now(), "Продукты", expenseOpType, account, food
		));
		OperationDto op2 = operationService.create(new OperationDto(
				null, BigDecimal.valueOf(2000), LocalDateTime.now(), "Зарплата", incomeOpType, account, salary
		));

		String filePath = "test-export.csv";

		// экспорт CSV
		exportService.exportData(ExportFormat.CSV, filePath);
		assertThat(new File(filePath)).exists();

		operationService.delete(op1.id());
		operationService.delete(op2.id());
		categoryService.delete(food.id());
		categoryService.delete(salary.id());
		accountService.delete(account.id());

		// импорт CSV
		importService.importData(ExportFormat.CSV, filePath);

		// данные восстановлены
		var accounts = accountService.findAll();
		var categories = categoryService.findAll();
		var operations = operationService.findAll();

		assertThat(accounts).hasSize(1);
		assertThat(categories).hasSize(2);
		assertThat(operations).hasSize(2);
	}
}
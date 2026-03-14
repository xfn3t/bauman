package ru.bauman.tigerbank.account.service.entity.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bauman.tigerbank.account.entity.BankAccount;
import ru.bauman.tigerbank.account.repository.BankAccountRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
class BankAccountEntityServiceIntegrationTest {

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
	private BankAccountEntityServiceImpl entityService;

	@Autowired
	private BankAccountRepository repository;

	@Test
	void saveAndGetById_ShouldWork() {
		BankAccount account = BankAccount.builder()
				.name("Тестовый счет")
				.balance(BigDecimal.valueOf(500))
				.build();

		BankAccount saved = entityService.save(account);
		assertThat(saved.getId()).isNotNull();

		BankAccount found = entityService.getById(saved.getId());
		assertThat(found.getName()).isEqualTo("Тестовый счет");
		assertThat(found.getBalance()).isEqualByComparingTo("500");
	}

	@Test
	void deleteById_ShouldRemoveEntity() {
		BankAccount account = BankAccount.builder()
				.name("Для удаления")
				.balance(BigDecimal.ZERO)
				.build();
		BankAccount saved = entityService.save(account);

		entityService.deleteById(saved.getId());

		assertThrows(RuntimeException.class, () -> entityService.getById(saved.getId()));
	}

	@Test
	void getAll_ShouldReturnAllAccounts() {
		BankAccount acc1 = BankAccount.builder().name("Счет1").balance(BigDecimal.TEN).build();
		BankAccount acc2 = BankAccount.builder().name("Счет2").balance(BigDecimal.ONE).build();
		entityService.save(acc1);
		entityService.save(acc2);

		var all = entityService.getAll();
		assertThat(all).hasSize(2);
	}
}
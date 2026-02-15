package ru.bauman.seminar.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:15-alpine")
	)
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	static {
		postgres.start();

		// Установите системные свойства для Spring
		System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
		System.setProperty("spring.datasource.username", postgres.getUsername());
		System.setProperty("spring.datasource.password", postgres.getPassword());
		System.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");
		System.setProperty("spring.liquibase.enabled", "false");
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
		registry.add("spring.liquibase.enabled", () -> false);
		registry.add("spring.flyway.enabled", () -> false);
	}
}
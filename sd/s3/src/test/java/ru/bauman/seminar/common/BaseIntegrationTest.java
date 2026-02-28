package ru.bauman.seminar.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;
import ru.bauman.seminar.satellite.creator.impl.CommunicationSatelliteCreator;
import ru.bauman.seminar.satellite.creator.impl.ImagingSatelliteCreator;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({
		TestContainersConfig.class,
		CommunicationSatelliteCreator.class,
		ImagingSatelliteCreator.class
})
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

	@DynamicPropertySource
	static void postgresProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", TestContainersConfig::getJdbcUrl);
		registry.add("spring.datasource.username", TestContainersConfig::getUsername);
		registry.add("spring.datasource.password", TestContainersConfig::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
	}
}
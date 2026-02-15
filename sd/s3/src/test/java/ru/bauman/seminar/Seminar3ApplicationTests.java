package ru.bauman.seminar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.bauman.seminar.common.TestContainersConfig;

@SpringBootTest
@Import(TestContainersConfig.class)
class Seminar3ApplicationTests {

	@Test
	void contextLoads() {
	}
}
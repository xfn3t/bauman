package ru.bmstu.sd.s2.satellite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import ru.bmstu.sd.s2.satellite.impl.CommunicationSatellite;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommunicationSatelliteTest {

	private CommunicationSatellite communicationSatellite;
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	@BeforeEach
	void setUp() {
		communicationSatellite = new CommunicationSatellite("TestComm", 0.5, 500.0);
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
	}

	@Test
	@DisplayName("Создание спутника связи с корректными параметрами")
	void constructorShouldSetCorrectValues() {
		assertEquals("TestComm", communicationSatellite.getName());
		assertEquals(0.5, communicationSatellite.getBatteryLevel(), 0.001);
		assertEquals(500.0, communicationSatellite.getBandwidth(), 0.001);
	}

	@Test
	@DisplayName("Выполнение миссии активным спутником")
	void performMissionActiveSatelliteShouldSendData() {

		communicationSatellite.activate();
		outputStream.reset();

		communicationSatellite.performMission();

		String output = outputStream.toString();
		assertTrue(output.contains("Передача данных"));
		assertTrue(output.contains("Отправил"));
	}

	@Test
	@DisplayName("Выполнение миссии неактивным спутником")
	void performMissionInactiveSatelliteShouldNotSendData() {

		outputStream.reset();

		communicationSatellite.performMission();

		String output = outputStream.toString();
		assertTrue(output.contains("Не может передавать данные"));
	}

	@Test
	@DisplayName("getBandwidth() возвращает правильное значение")
	void getBandwidthShouldReturnCorrectValue() {

		CommunicationSatellite sat = new CommunicationSatellite("Связь-1", 0.8, 1000.0);

		assertEquals(1000.0, sat.getBandwidth(), 0.001);
	}

	@Test
	@DisplayName("toString() возвращает корректную строку для CommunicationSatellite")
	void toStringShouldIncludeCommunicationSpecificFields() {

		CommunicationSatellite sat = new CommunicationSatellite("Связь-Тест", 0.85, 750.0);

		String result = sat.toString();

		assertTrue(result.contains("CommunicationSatellite"));
		assertTrue(result.contains("bandwidth=750.0"));
	}

	@Test
	@DisplayName("Потребление энергии при выполнении миссии")
	void performMissionShouldConsumeBattery() {

		communicationSatellite.activate();
		double initialBattery = communicationSatellite.getBatteryLevel();
		outputStream.reset();

		communicationSatellite.performMission();

		assertEquals(initialBattery - 0.05, communicationSatellite.getBatteryLevel(), 0.001);
	}
}
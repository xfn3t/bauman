package ru.bmstu.sd.s1.satellite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImagingSatelliteTest {

	private ImagingSatellite imagingSatellite;
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	@BeforeEach
	void setUp() {
		imagingSatellite = new ImagingSatellite("TestImaging", 0.5, 2.0);
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
	}

	@Test
	@DisplayName("Создание спутника ДЗЗ с корректными параметрами")
	void constructorShouldSetCorrectValues() {
		assertEquals("TestImaging", imagingSatellite.getName());
		assertEquals(0.5, imagingSatellite.getBatteryLevel(), 0.001);
		assertEquals(2.0, imagingSatellite.getResolution(), 0.001);
		assertEquals(0, imagingSatellite.getPhotosTaken());
	}

	@Test
	@DisplayName("Выполнение миссии активным спутником")
	void performMissionActiveSatelliteShouldTakePhoto() {

		imagingSatellite.activate();
		outputStream.reset();

		imagingSatellite.performMission();

		String output = outputStream.toString(StandardCharsets.UTF_8).toLowerCase();
		assertTrue(output.contains("съемка территории"));
		assertTrue(output.contains("снимок"));
		assertEquals(1, imagingSatellite.getPhotosTaken());
	}

	@Test
	@DisplayName("Выполнение миссии неактивным спутником")
	void performMissionInactiveSatelliteShouldNotTakePhoto() {
		outputStream.reset();

		imagingSatellite.performMission();

		String output = outputStream.toString();
		assertTrue(output.contains("Не может выполнить съемку"));
		assertEquals(0, imagingSatellite.getPhotosTaken());
	}

	@Test
	@DisplayName("takePhoto() увеличивает счетчик фотографий")
	void takePhotoShouldIncrementPhotosTaken() {
		imagingSatellite.activate();

		// Используем reflection для вызова private метода или тестируем через performMission
		imagingSatellite.performMission();

		assertEquals(1, imagingSatellite.getPhotosTaken());
	}

	@Test
	@DisplayName("getters возвращают правильные значения")
	void gettersShouldReturnCorrectValues() {
		ImagingSatellite sat = new ImagingSatellite("ДЗЗ-1", 0.8, 1.5);

		assertEquals("ДЗЗ-1", sat.getName());
		assertEquals(0.8, sat.getBatteryLevel(), 0.001);
		assertEquals(1.5, sat.getResolution(), 0.001);
		assertEquals(0, sat.getPhotosTaken());
	}

	@Test
	@DisplayName("toString() возвращает корректную строку для ImagingSatellite")
	void toStringShouldIncludeImagingSpecificFields() {
		ImagingSatellite sat = new ImagingSatellite("ДЗЗ-Тест", 0.75, 3.0);

		String result = sat.toString();

		assertTrue(result.contains("ImagingSatellite"));
		assertTrue(result.contains("resolution=3.0"));
		assertTrue(result.contains("photosTaken=0"));
	}
}
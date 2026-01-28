package ru.bmstu.sd.s2.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.bmstu.sd.s2.satellite.impl.CommunicationSatellite;
import ru.bmstu.sd.s2.satellite.impl.ImagingSatellite;
import ru.bmstu.sd.s2.satellite.Satellite;
import ru.bmstu.sd.s2.satellite.constellation.SatelliteConstellation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

	@Test
	@DisplayName("Полный цикл работы системы")
	void fullSystemWorkflow() {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStream));

		SatelliteConstellation constellation = new SatelliteConstellation("IntegrationTest");

		CommunicationSatellite commSat = new CommunicationSatellite("Интеграция-Связь", 0.9, 800.0);
		ImagingSatellite imgSat = new ImagingSatellite("Интеграция-ДЗЗ", 0.3, 1.0);

		constellation.addSatellite(commSat);
		constellation.addSatellite(imgSat);

		constellation.activateAllSatellites();
		constellation.executeAllMissions();

		String output = outputStream.toString();
		System.setOut(originalOut);

		assertTrue(commSat.isActive());
		assertTrue(imgSat.isActive());

		assertTrue(output.contains("Передача данных"));
		assertTrue(output.contains("Съемка территории"));

		assertEquals(1, imgSat.getPhotosTaken());
	}

	@Test
	@DisplayName("Полиморфизм: работа с Satellite через базовый класс")
	void polymorphismThroughBaseClass() {

		Satellite satellite1 = new CommunicationSatellite("Poly1", 0.5, 100.0);
		Satellite satellite2 = new ImagingSatellite("Poly2", 0.5, 2.0);

		boolean activated1;
		boolean activated2;

		activated1 = satellite1.activate();
		activated2 = satellite2.activate();

		assertTrue(activated1);
		assertTrue(activated2);
		assertTrue(satellite1.isActive());
		assertTrue(satellite2.isActive());

		assertInstanceOf(CommunicationSatellite.class, satellite1);
		assertInstanceOf(ImagingSatellite.class, satellite2);
	}

	@Test
	@DisplayName("Интеграция: группировка с несколькими типами спутников")
	void integrationMixedSatelliteTypes() {

		SatelliteConstellation constellation = new SatelliteConstellation("MixedTest");

		Satellite comm1 = new CommunicationSatellite("Comm1", 0.8, 500.0);
		Satellite comm2 = new CommunicationSatellite("Comm2", 0.25, 300.0);
		Satellite img1 = new ImagingSatellite("Img1", 0.9, 1.5);
		Satellite img2 = new ImagingSatellite("Img2", 0.15, 2.0);

		constellation.addSatellite(comm1);
		constellation.addSatellite(comm2);
		constellation.addSatellite(img1);
		constellation.addSatellite(img2);

		constellation.activateAllSatellites();

		assertTrue(comm1.isActive());
		assertTrue(comm2.isActive());
		assertTrue(img1.isActive());
		assertFalse(img2.isActive());

		assertEquals(4, constellation.getSatellites().size());
	}

	@Test
	@DisplayName("Интеграция: последовательное выполнение миссий")
	void integrationSequentialMissions() {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(outputStream));

		ImagingSatellite imgSat = new ImagingSatellite("Тест-ДЗЗ", 0.5, 1.0);
		SatelliteConstellation constellation = new SatelliteConstellation("Test");
		constellation.addSatellite(imgSat);

		imgSat.activate();

		for (int i = 0; i < 3; i++) {
			constellation.executeAllMissions();
		}

		String output = outputStream.toString();
		System.setOut(originalOut);

		assertEquals(3, imgSat.getPhotosTaken());
		assertTrue(imgSat.getBatteryLevel() < 0.5);

		assertTrue(output.contains("Тест-ДЗЗ"));
		assertTrue(output.contains("Съемка территории"));
	}
}
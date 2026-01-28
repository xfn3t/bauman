package ru.bmstu.sd.s2.satellite;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.bmstu.sd.s2.satellite.constellation.SatelliteConstellation;
import ru.bmstu.sd.s2.satellite.impl.CommunicationSatellite;
import ru.bmstu.sd.s2.satellite.impl.ImagingSatellite;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SatelliteConstellationTest {

	private SatelliteConstellation constellation;
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;

	@BeforeEach
	void setUp() {
		constellation = new SatelliteConstellation("TestConstellation");
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
	}

	@Test
	@DisplayName("Добавление спутника в группировку")
	void addSatelliteShouldAddToCollection() {

		Satellite sat = new CommunicationSatellite("TestSat", 0.5, 100.0);
		outputStream.reset();

		constellation.addSatellite(sat);

		String output = outputStream.toString();
		assertTrue(output.contains("добавлен в группировку"));
		List<Satellite> satellites = constellation.getSatellites();
		assertEquals(1, satellites.size());
		assertEquals("TestSat", satellites.get(0).getName());
	}

	@Test
	@DisplayName("Получение списка спутников")
	void getSatellitesShouldReturnCopyOfList() {

		Satellite sat1 = new CommunicationSatellite("Sat1", 0.5, 100.0);
		Satellite sat2 = new ImagingSatellite("Sat2", 0.6, 2.0);
		constellation.addSatellite(sat1);
		constellation.addSatellite(sat2);

		List<Satellite> satellites = constellation.getSatellites();

		assertEquals(2, satellites.size());

		// Проверяем, что это копия, а не оригинальный список
		satellites.clear();
		List<Satellite> original = constellation.getSatellites();
		assertEquals(2, original.size());
	}

	@Test
	@DisplayName("Активация всех спутников в группировке")
	void activateAllSatellitesShouldActivateEligibleSatellites() {

		Satellite sat1 = new CommunicationSatellite("Sat1", 0.5, 100.0);
		Satellite sat2 = new ImagingSatellite("Sat2", 0.1, 2.0);
		Satellite sat3 = new CommunicationSatellite("Sat3", 0.3, 100.0);

		constellation.addSatellite(sat1);
		constellation.addSatellite(sat2);
		constellation.addSatellite(sat3);

		outputStream.reset();

		constellation.activateAllSatellites();

		String output = outputStream.toString();
		assertTrue(output.contains("АКТИВАЦИЯ СПУТНИКОВ"));
		assertTrue(sat1.isActive());
		assertFalse(sat2.isActive());
		assertTrue(sat3.isActive());
	}

	@Test
	@DisplayName("Выполнение всех миссий в группировке")
	void executeAllMissionsShouldExecuteForAllSatellites() {

		CommunicationSatellite sat1 = new CommunicationSatellite("Связь-1", 0.5, 100.0);
		ImagingSatellite sat2 = new ImagingSatellite("ДЗЗ-1", 0.6, 2.0);

		sat1.activate();
		sat2.activate();

		constellation.addSatellite(sat1);
		constellation.addSatellite(sat2);

		outputStream.reset();

		constellation.executeAllMissions();

		String output = outputStream.toString();
		assertTrue(output.contains("ВЫПОЛНЕНИЕ МИССИЙ"));
		assertTrue(output.contains("Связь-1"));
		assertTrue(output.contains("ДЗЗ-1"));
		assertEquals(1, sat2.getPhotosTaken());
	}

	@Test
	@DisplayName("Смешанная группировка с разными типами спутников")
	void mixedConstellationShouldHandleAllTypes() {

		Satellite[] satellites = {
				new CommunicationSatellite("Comm1", 0.8, 200.0),
				new CommunicationSatellite("Comm2", 0.25, 300.0),
				new ImagingSatellite("Img1", 0.9, 1.5),
				new ImagingSatellite("Img2", 0.15, 2.0)
		};

		for (Satellite sat : satellites) {
			constellation.addSatellite(sat);
		}

		List<Satellite> result = constellation.getSatellites();

		assertEquals(4, result.size());
		assertInstanceOf(CommunicationSatellite.class, result.get(0));
		assertInstanceOf(ImagingSatellite.class, result.get(2));
	}

	@Test
	@DisplayName("Создание группировки с именем")
	void constructorShouldSetConstellationName() {

		SatelliteConstellation constel = new SatelliteConstellation("Галактика");

		Satellite sat = new CommunicationSatellite("Sat1", 0.5, 100.0);
		outputStream.reset();

		constel.addSatellite(sat);

		String output = outputStream.toString();
		assertTrue(output.contains("Галактика"));
	}
}
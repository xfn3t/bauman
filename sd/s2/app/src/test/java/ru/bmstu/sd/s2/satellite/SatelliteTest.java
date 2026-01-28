package ru.bmstu.sd.s2.satellite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.bmstu.sd.s2.satellite.impl.CommunicationSatellite;

import static org.junit.jupiter.api.Assertions.*;

public class SatelliteTest {

	private Satellite satellite;

	@BeforeEach
	void setUp() {
		satellite = new CommunicationSatellite("TestSat", 0.5, 100.0);
	}

	@Test
	@DisplayName("Активация при достаточном заряде")
	void activateWithSufficientBatteryShouldActivate() {
		Satellite sat = new CommunicationSatellite("Test", 0.3, 100.0);

		boolean result = sat.activate();

		assertTrue(result);
		assertTrue(sat.isActive());
	}

	@Test
	@DisplayName("Активация при низком заряде должна завершиться неудачей")
	void activateWithLowBatteryShouldFail() {
		Satellite sat = new CommunicationSatellite("Test", 0.1, 100.0);

		boolean result = sat.activate();

		assertFalse(result);
		assertFalse(sat.isActive());
	}

	@Test
	@DisplayName("Деактивация активного спутника")
	void deactivateActiveSatelliteShouldDeactivate() {
		satellite.activate();

		satellite.deactivate();

		assertFalse(satellite.isActive());
	}

	@Test
	@DisplayName("Потребление энергии уменьшает заряд")
	void consumeBatteryShouldReduceBatteryLevel() {
		satellite.activate();
		double initialBattery = satellite.getBatteryLevel();

		satellite.consumeBattery(0.1);

		assertEquals(initialBattery - 0.1, satellite.getBatteryLevel(), 0.001);
	}

	@Test
	@DisplayName("Потребление энергии до критического уровня выключает спутник")
	void consumeBatteryBelowThresholdShouldDeactivate() {
		Satellite sat = new CommunicationSatellite("Test", 0.25, 100.0);
		sat.activate();

		sat.consumeBattery(0.1);

		assertFalse(sat.isActive());
	}

	@Test
	@DisplayName("Геттеры возвращают правильные значения")
	void gettersShouldReturnCorrectValues() {
		Satellite sat = new CommunicationSatellite("Satellite1", 0.75, 200.0);

		assertEquals("Satellite1", sat.getName());
		assertEquals(0.75, sat.getBatteryLevel(), 0.001);
		assertFalse(sat.isActive());
	}

	@Test
	@DisplayName("toString() возвращает корректную строку")
	void toStringShouldReturnCorrectFormat() {
		Satellite sat = new CommunicationSatellite("TestSat", 0.5, 100.0);

		String result = sat.toString();

		assertTrue(result.contains("TestSat"));
		assertTrue(result.contains("0.50"));
	}
}
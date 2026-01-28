package ru.bmstu.sd.s2.satellite;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnergySystemTest {

	@Test
	@DisplayName("Создание системы энергии с начальным зарядом")
	void constructorShouldSetInitialBatteryLevel() {
		EnergySystem energy = new EnergySystem(0.75);
		assertEquals(0.75, energy.getBatteryLevel(), 0.001);
	}

	@Test
	@DisplayName("Потребление энергии уменьшает заряд")
	void consumeShouldReduceBatteryLevel() {
		EnergySystem energy = new EnergySystem(0.5);
		energy.consume(0.1);
		assertEquals(0.4, energy.getBatteryLevel(), 0.001);
	}

	@Test
	@DisplayName("Потребление не может сделать заряд отрицательным")
	void consumeShouldNotGoBelowZero() {
		EnergySystem energy = new EnergySystem(0.05);
		energy.consume(0.1);
		assertEquals(0.0, energy.getBatteryLevel(), 0.001);
	}

	@Test
	@DisplayName("hasSufficientCharge возвращает true при заряде выше критического")
	void hasSufficientChargeShouldReturnTrueWhenAboveCritical() {
		EnergySystem energy = new EnergySystem(0.3);
		assertTrue(energy.hasSufficientCharge());
	}

	@Test
	@DisplayName("hasSufficientCharge возвращает false при критическом заряде")
	void hasSufficientChargeShouldReturnFalseWhenCritical() {
		EnergySystem energy = new EnergySystem(0.2);
		assertFalse(energy.hasSufficientCharge());
	}
}
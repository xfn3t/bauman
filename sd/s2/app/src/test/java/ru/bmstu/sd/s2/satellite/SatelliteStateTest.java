package ru.bmstu.sd.s2.satellite;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SatelliteStateTest {

	@Test
	@DisplayName("Создание состояния с неактивным статусом")
	void initialStateShouldBeInactive() {
		SatelliteState state = new SatelliteState();
		assertFalse(state.isActive());
	}

	@Test
	@DisplayName("Активация при наличии достаточного заряда")
	void activateWithSufficientChargeShouldSucceed() {
		SatelliteState state = new SatelliteState();
		boolean result = state.activate(true);
		assertTrue(result);
		assertTrue(state.isActive());
	}

	@Test
	@DisplayName("Активация без достаточного заряда должна завершиться неудачей")
	void activateWithoutSufficientChargeShouldFail() {
		SatelliteState state = new SatelliteState();
		boolean result = state.activate(false);
		assertFalse(result);
		assertFalse(state.isActive());
	}

	@Test
	@DisplayName("Деактивация активного состояния")
	void deactivateActiveStateShouldWork() {
		SatelliteState state = new SatelliteState();
		state.activate(true);
		state.deactivate();
		assertFalse(state.isActive());
	}
}
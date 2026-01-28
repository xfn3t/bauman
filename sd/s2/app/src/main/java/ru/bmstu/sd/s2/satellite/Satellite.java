package ru.bmstu.sd.s2.satellite;

import java.util.Locale;

public abstract class Satellite {

	private final String name;
	protected final SatelliteState state;
	protected final EnergySystem energy;

	public Satellite(String name, double initialBatteryLevel) {
		this.name = name;
		this.energy = new EnergySystem(initialBatteryLevel);
		this.state = new SatelliteState();
	}

	public boolean activate() {
		boolean activated = state.activate(energy.hasSufficientCharge());
		if (activated) {
			System.out.println("✅ " + getName() + ": Активация успешна");
		} else {
			int batteryPercent = (int)(energy.getBatteryLevel() * 100);
			System.out.println("🛑 " + getName() +
					": Ошибка активации (заряд: " + batteryPercent + "%)");
		}
		return activated;
	}

	public void deactivate() {
		state.deactivate();
	}

	protected void consumeBattery(double amount) {
		if (!state.isActive()) return;

		energy.consume(amount);
		if (energy.isCritical()) {
			state.deactivate();
			System.out.println("⚠️ " + getName() + ": Критический уровень заряда, деактивация");
		}
	}

	public abstract void performMission();

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return state.isActive();
	}

	public double getBatteryLevel() {
		return energy.getBatteryLevel();
	}

	@Override
	public String toString() {
		return String.format(Locale.US,
				"%s{name='%s', isActive=%s, batteryLevel=%.2f}",
				getClass().getSimpleName(), name, state.isActive(), energy.getBatteryLevel());
	}
}
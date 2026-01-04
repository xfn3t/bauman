package ru.bmstu.sd.s1.satellite;

import java.util.Locale;

public abstract class Satellite {

	private final String name;
	private boolean isActive;
	private double batteryLevel;
	private final double CRITICAL_BATTERY_LEVEL = 0.2;

	public Satellite(String name, double batteryLevel) {
		this.name = name;
		this.batteryLevel = batteryLevel;
		this.isActive = false;
	}

	public boolean activate() {
		if (batteryLevel > CRITICAL_BATTERY_LEVEL && !isActive) {
			isActive = true;
			return true;
		}
		return false;
	}

	public void deactivate() {
		isActive = false;
	}

	public void consumeBattery(double amount) {
		if (!isActive) return;

		batteryLevel -= amount;
		if (batteryLevel <= CRITICAL_BATTERY_LEVEL) {
			isActive = false;
		}
	}

	protected abstract void performMission();

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return isActive;
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	@Override
	public String toString() {
		return String.format(Locale.US,
				"%s{name='%s', isActive=%s, batteryLevel=%.2f}",
				getClass().getSimpleName(), name, isActive, batteryLevel);
	}
}
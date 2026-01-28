package ru.bmstu.sd.s2.satellite;

public class EnergySystem {
	private double batteryLevel;
	private static final double CRITICAL_BATTERY_LEVEL = 0.2;

	public EnergySystem(double initialBatteryLevel) {
		this.batteryLevel = initialBatteryLevel;
	}

	public void consume(double amount) {
		if (batteryLevel <= 0) return;

		batteryLevel = Math.max(0, batteryLevel - amount);
	}

	public void recharge(double amount) {
		batteryLevel = Math.min(1.0, batteryLevel + amount);
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	public boolean hasSufficientCharge() {
		return batteryLevel > CRITICAL_BATTERY_LEVEL;
	}

	public boolean isCritical() {
		return batteryLevel <= CRITICAL_BATTERY_LEVEL;
	}
}
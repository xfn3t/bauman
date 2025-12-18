package ru.bmstu.sd.s1.satellite;

import java.util.Locale;

public class CommunicationSatellite extends Satellite {

	private final double bandWidth;

	public CommunicationSatellite(String name, double batteryLevel, double bandWidth) {
		super(name, batteryLevel);
		this.bandWidth = bandWidth;
	}

	public double getBandwidth() {
		return bandWidth;
	}

	private void sendData(double amount) {
		if (isActive()) {
			System.out.println(getName() + ": Отправил " + amount + " Мбит данных!");
		}
	}

	@Override
	protected void performMission() {
		if (isActive()) {
			System.out.println(getName() + ": Передача данных со скоростью " + bandWidth + " Мбит/с");
			sendData(bandWidth);
			consumeBattery(0.05);
		} else {
			System.out.println("🛑 " + getName() + ": Не может передавать данные - не активен");
		}
	}

	@Override
	public String toString() {
		return String.format(Locale.US,
				"CommunicationSatellite{bandwidth=%.1f, name='%s', isActive=%s, batteryLevel=%.2f}",
				bandWidth, getName(), isActive(), getBatteryLevel());
	}
}
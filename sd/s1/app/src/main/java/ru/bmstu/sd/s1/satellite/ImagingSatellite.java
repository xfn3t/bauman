package ru.bmstu.sd.s1.satellite;

import java.util.Locale;

public class ImagingSatellite extends Satellite {

	private final double resolution;
	private int photosTaken;

	public ImagingSatellite(String name, double batteryLevel, double resolution) {
		super(name, batteryLevel);
		this.resolution = resolution;
		this.photosTaken = 0;
	}

	public double getResolution() {
		return resolution;
	}

	public int getPhotosTaken() {
		return photosTaken;
	}

	private void takePhoto() {
		if (isActive()) {
			photosTaken++;
			System.out.println(getName() + ": Снимок #" + photosTaken + " сделан!");
		}
	}

	@Override
	protected void performMission() {
		if (isActive()) {
			System.out.println(getName() + ": Съемка территории с разрешением " + resolution + " м/пиксель");
			takePhoto();
			consumeBattery(0.08);
		} else {
			System.out.println("🛑 " + getName() + ": Не может выполнить съемку - не активен");
		}
	}

	@Override
	public String toString() {
		return String.format(Locale.US,
				"ImagingSatellite{resolution=%.1f, photosTaken=%d, name='%s', isActive=%s, batteryLevel=%.2f}",
				resolution, photosTaken, getName(), isActive(), getBatteryLevel());
	}
}
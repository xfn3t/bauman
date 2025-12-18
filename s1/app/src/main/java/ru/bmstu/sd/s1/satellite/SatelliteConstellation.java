package ru.bmstu.sd.s1.satellite;

import java.util.ArrayList;
import java.util.List;

public class SatelliteConstellation {

	private final String constellationName;
	private final List<Satellite> satellites;

	public SatelliteConstellation(String constellationName) {
		this.constellationName = constellationName;
		this.satellites = new ArrayList<>();
	}

	public void addSatellite(Satellite satellite) {
		satellites.add(satellite);
		System.out.println(satellite.getName() + " добавлен в группировку '" + constellationName + "'");
	}

	public void executeAllMissions() {
		System.out.println("\nВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ " + constellationName.toUpperCase());
		System.out.println("=".repeat(50));
		for (Satellite satellite : satellites) {
			satellite.performMission();
		}
	}

	public List<Satellite> getSatellites() {
		return new ArrayList<>(satellites);
	}

	public void activateAllSatellites() {
		System.out.println("\nАКТИВАЦИЯ СПУТНИКОВ:");
		System.out.println("-".repeat(25));
		for (Satellite satellite : satellites) {
			boolean activated = satellite.activate();
			if (activated) {
				System.out.println("✅ " + satellite.getName() + ": Активация успешна");
			} else {
				int batteryPercent = (int)(satellite.getBatteryLevel() * 100);
				System.out.println("🛑 " + satellite.getName() +
						": Ошибка активации (заряд: " + batteryPercent + "%)");
			}
		}
	}
}
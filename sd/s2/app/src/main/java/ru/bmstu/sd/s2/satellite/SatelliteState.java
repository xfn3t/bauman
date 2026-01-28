package ru.bmstu.sd.s2.satellite;

public class SatelliteState {

	private boolean isActive;

	public SatelliteState() {
		this.isActive = false;
	}

	public boolean activate(boolean hasSufficientCharge) {
		if (hasSufficientCharge && !isActive) {
			isActive = true;
			return true;
		}
		return false;
	}

	public void deactivate() {
		isActive = false;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}
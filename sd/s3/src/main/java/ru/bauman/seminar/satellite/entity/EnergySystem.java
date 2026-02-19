package ru.bauman.seminar.satellite.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class EnergySystem {

	private static final BigDecimal CRITICAL_THRESHOLD = new BigDecimal("0.2");

	private BigDecimal batteryLevel;

	public EnergySystem(BigDecimal initialBatteryLevel) {
		// округляем до двух знаков
		this.batteryLevel = initialBatteryLevel.setScale(2, RoundingMode.HALF_UP);
	}

	public void consume(BigDecimal amount) {
		batteryLevel = batteryLevel.subtract(amount).max(BigDecimal.ZERO)
				.setScale(2, RoundingMode.HALF_UP);
	}

	public boolean hasSufficientCharge() {
		return batteryLevel.compareTo(CRITICAL_THRESHOLD) > 0;
	}

	public boolean isCritical() {
		return batteryLevel.compareTo(CRITICAL_THRESHOLD) <= 0;
	}

	public void recharge(BigDecimal amount) {
		batteryLevel = batteryLevel.add(amount).min(BigDecimal.ONE)
				.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public String toString() {
		return String.format("EnergySystem{batteryLevel=%.2f}", batteryLevel);
	}
}
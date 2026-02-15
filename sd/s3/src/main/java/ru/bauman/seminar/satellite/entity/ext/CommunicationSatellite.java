package ru.bauman.seminar.satellite.entity.ext;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "communication_satellites")
@DiscriminatorValue("COMMUNICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class CommunicationSatellite extends Satellite {

	private static final BigDecimal ENERGY_CONSUMPTION = new BigDecimal("0.05");
	private static final BigDecimal CRITICAL_BATTERY_THRESHOLD = new BigDecimal("0.2");

	@Column(name = "bandwidth")
	private BigDecimal bandwidth;

	@Override
	public SatelliteType getType() {
		return SatelliteType.COMMUNICATION;
	}

	@Override
	public void performMission() {
		if (Boolean.TRUE.equals(getActive())) {
			BigDecimal newBattery = getBatteryLevel().subtract(ENERGY_CONSUMPTION)
					.max(BigDecimal.ZERO)
					.setScale(2, RoundingMode.HALF_UP);
			setBatteryLevel(newBattery);
			log.info("{}: Передача данных, скорость {} Мбит/с", getName(), bandwidth);
			if (getBatteryLevel().compareTo(CRITICAL_BATTERY_THRESHOLD) <= 0) {
				setActive(false);
				log.warn("⚠️ {}: Критический заряд, деактивация", getName());
			}
		} else {
			log.info("🛑 {}: Неактивен, миссия невозможна", getName());
		}
	}
}
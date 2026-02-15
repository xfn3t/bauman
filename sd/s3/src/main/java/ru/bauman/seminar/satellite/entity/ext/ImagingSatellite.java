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
@Table(name = "imaging_satellites")
@DiscriminatorValue("IMAGING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class ImagingSatellite extends Satellite {

	private static final BigDecimal ENERGY_CONSUMPTION = new BigDecimal("0.08");
	private static final BigDecimal CRITICAL_BATTERY_THRESHOLD = new BigDecimal("0.2");

	@Column(name = "resolution")
	private BigDecimal resolution;

	@Builder.Default
	@Column(name = "photos_taken")
	private Integer photosTaken = 0;

	@Override
	public SatelliteType getType() {
		return SatelliteType.IMAGING;
	}

	@Override
	public void performMission() {
		if (Boolean.TRUE.equals(getActive())) {
			BigDecimal newBattery = getBatteryLevel().subtract(ENERGY_CONSUMPTION)
					.max(BigDecimal.ZERO)
					.setScale(2, RoundingMode.HALF_UP);
			setBatteryLevel(newBattery);
			photosTaken++;
			log.info("{}: Съёмка, разрешение {} м/пиксель, снимков: {}",
					getName(), resolution, photosTaken);
			if (getBatteryLevel().compareTo(CRITICAL_BATTERY_THRESHOLD) <= 0) {
				setActive(false);
				log.warn("⚠️ {}: Критический заряд, деактивация", getName());
			}
		} else {
			log.info("🛑 {}: Неактивен, миссия невозможна", getName());
		}
	}
}
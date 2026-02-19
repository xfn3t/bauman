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
			log.info("{}: Передача данных, скорость {} Мбит/с", getName(), bandwidth);
			consumeBattery(ENERGY_CONSUMPTION);
		} else {
			log.info("🛑 {}: Неактивен, миссия невозможна", getName());
		}
	}
}
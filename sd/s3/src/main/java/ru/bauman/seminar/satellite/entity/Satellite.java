package ru.bauman.seminar.satellite.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "satellites")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Slf4j
public abstract class Satellite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private SatelliteState state = SatelliteState.INACTIVE;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "constellation_id")
	private Constellation constellation;

	@Column(nullable = false, updatable = false)
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();

	private LocalDateTime updatedAt;

	@Embedded
	private EnergySystem energySystem;

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public abstract void performMission();

	public abstract SatelliteType getType();

	public BigDecimal getBatteryLevel() {
		return energySystem.getBatteryLevel();
	}

	public boolean activate() {
		if (state == SatelliteState.ACTIVE) {
			return false;
		}
		if (energySystem.hasSufficientCharge()) {
			state = SatelliteState.ACTIVE;
			return true;
		}
		return false;
	}

	public boolean deactivate() {
		if (state == SatelliteState.INACTIVE) return false;
		state = SatelliteState.INACTIVE;
		return true;
	}

	protected void consumeBattery(BigDecimal amount) {
		energySystem.consume(amount);
		if (energySystem.isCritical()) {
			state = SatelliteState.CRITICAL;
			log.warn("⚠️ {}: Критический заряд, состояние CRITICAL", getName());
		}
	}
}
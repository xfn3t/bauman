package ru.bauman.seminar.satellite.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
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
public abstract class Satellite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private BigDecimal batteryLevel;

	@Column(nullable = false)
	@Builder.Default
	private Boolean active = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "constellation_id")
	private Constellation constellation;

	@Column(nullable = false, updatable = false)
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();

	private LocalDateTime updatedAt;

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public abstract void performMission();

	public abstract SatelliteType getType();
}
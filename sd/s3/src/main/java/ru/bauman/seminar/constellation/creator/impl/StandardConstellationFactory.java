package ru.bauman.seminar.constellation.creator.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.seminar.constellation.creator.ConstellationFactory;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.creator.SatelliteFactory;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class StandardConstellationFactory implements ConstellationFactory {

	private final SatelliteFactory satelliteFactory;

	@Override
	public Constellation createConstellation(String name, String description) {
		return Constellation.builder()
				.name(name)
				.description(description)
				.build();
	}

	@Override
	public Constellation createDefaultConstellation() {
		return createConstellation("Default Group", "Standard empty group");
	}

	@Override
	public Constellation createConstellationWithDefaultSatellites(String name, String description) {
		Satellite commSat = satelliteFactory.createSatellite(
				new SatelliteRequest("DefaultComm", BigDecimal.valueOf(0.9), SatelliteType.COMMUNICATION,
						BigDecimal.valueOf(500), null)
		);
		Satellite imgSat = satelliteFactory.createSatellite(
				new SatelliteRequest("DefaultImg", BigDecimal.valueOf(0.8), SatelliteType.IMAGING,
						null, BigDecimal.valueOf(2.5))
		);
		return Constellation.builder()
				.name(name)
				.description(description)
				.addSatellite(commSat)
				.addSatellite(imgSat)
				.build();
	}
}
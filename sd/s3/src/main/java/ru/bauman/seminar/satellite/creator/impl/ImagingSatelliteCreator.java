package ru.bauman.seminar.satellite.creator.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.creator.SatelliteCreator;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.ext.ImagingSatellite;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class ImagingSatelliteCreator implements SatelliteCreator {

	@Override
	public SatelliteType getType() {
		return SatelliteType.IMAGING;
	}

	@Override
	public Satellite create(SatelliteRequest request) {
		return ImagingSatellite.builder()
				.name(request.name())
				.energySystem(EnergySystem.builder().batteryLevel(request.batteryLevel()).build())
				.state(SatelliteState.INACTIVE)
				.resolution(request.resolution())
				.photosTaken(0)
				.build();
	}
}
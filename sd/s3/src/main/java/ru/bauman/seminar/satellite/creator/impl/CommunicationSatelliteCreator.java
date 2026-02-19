package ru.bauman.seminar.satellite.creator.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.creator.SatelliteCreator;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.ext.CommunicationSatellite;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class CommunicationSatelliteCreator implements SatelliteCreator {

	@Override
	public SatelliteType getType() {
		return SatelliteType.COMMUNICATION;
	}

	@Override
	public Satellite create(SatelliteRequest request) {
		return CommunicationSatellite.builder()
				.name(request.name())
				.energySystem(new EnergySystem(request.batteryLevel()))
				.active(false)
				.bandwidth(request.bandwidth())
				.build();
	}
}
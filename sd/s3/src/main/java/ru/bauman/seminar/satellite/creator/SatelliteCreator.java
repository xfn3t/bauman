package ru.bauman.seminar.satellite.creator;

import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

public interface SatelliteCreator {
	SatelliteType getType();
	Satellite create(SatelliteRequest request);
}
package ru.bauman.seminar.satellite.creator;

import org.springframework.stereotype.Component;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class SatelliteFactory {

	private final Map<SatelliteType, SatelliteCreator> creators = new EnumMap<>(SatelliteType.class);

	public SatelliteFactory(List<SatelliteCreator> creatorList) {
		creatorList.forEach(creator -> creators.put(creator.getType(), creator));
	}

	public Satellite createSatellite(SatelliteRequest request) {
		SatelliteCreator creator = creators.get(request.type());
		if (creator == null) {
			throw new IllegalArgumentException("Неизвестный тип спутника: " + request.type());
		}
		return creator.create(request);
	}
}
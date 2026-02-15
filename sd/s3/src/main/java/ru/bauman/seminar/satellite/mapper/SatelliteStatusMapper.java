package ru.bauman.seminar.satellite.mapper;

import org.mapstruct.Mapper;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteStatusDto;
import ru.bauman.seminar.satellite.entity.Satellite;

@Mapper(componentModel = "spring", uses = {CommunicationSatelliteMapper.class, ImagingSatelliteMapper.class})
public interface SatelliteStatusMapper {
	SatelliteStatusDto toDto(Satellite satellite);
}
package ru.bauman.seminar.satellite.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.creator.SatelliteCreator;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.mapper.SatelliteMapper;
import ru.bauman.seminar.satellite.service.entity.SatelliteEntityService;
import ru.bauman.seminar.satellite.service.SatelliteService;
import ru.bauman.seminar.satellite.updater.SatelliteUpdater;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SatelliteServiceImpl implements SatelliteService {

	private final SatelliteEntityService satelliteEntityService;
	private final Map<SatelliteType, SatelliteCreator> creators;
	private final Map<SatelliteType, SatelliteUpdater> updaters;
	private final SatelliteMapper mapper;

	private static final BigDecimal MIN_BATTERY_FOR_ACTIVATION = new BigDecimal("0.2");

	public SatelliteServiceImpl(
			List<SatelliteCreator> creatorList,
			List<SatelliteUpdater> updaterList,
			SatelliteMapper mapper,
			SatelliteEntityService satelliteEntityService
	) {
		this.creators = creatorList.stream()
				.collect(Collectors.toMap(SatelliteCreator::getType, Function.identity()));
		this.updaters = updaterList.stream()
				.collect(Collectors.toMap(SatelliteUpdater::getType, Function.identity()));
		this.mapper = mapper;
		this.satelliteEntityService = satelliteEntityService;
	}

	@Transactional(readOnly = true)
	public List<SatelliteResponse> findAll() {
		return satelliteEntityService.findAll().stream()
				.map(mapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public SatelliteResponse findById(Long id) {
		Satellite satellite = satelliteEntityService.findById(id);
		return mapper.toResponse(satellite);
	}

	@Transactional(readOnly = true)
	public SatelliteResponse findByName(String name) {
		Satellite satellite = satelliteEntityService.findByName(name);
		return mapper.toResponse(satellite);
	}

	@Transactional
	public SatelliteResponse create(SatelliteRequest request) {

		SatelliteCreator creator = creators.get(request.type());
		if (creator == null) {
			throw new IllegalArgumentException("Неизвестный тип спутника: " + request.type());
		}

		Satellite satellite = creator.create(request);
		satellite = satelliteEntityService.save(satellite);

		log.info("Создан спутник: {} типа {}", satellite.getName(), satellite.getType());
		return mapper.toResponse(satellite);
	}

	@Transactional
	public SatelliteResponse update(Long id, SatelliteRequest request) {
		Satellite satellite = satelliteEntityService.findById(id);
		if (!satellite.getType().equals(request.type())) {
			throw new IllegalArgumentException("Нельзя изменить тип спутника");
		}
		satellite.setName(request.name());
		satellite.setEnergySystem(new EnergySystem(request.batteryLevel()));

		SatelliteUpdater updater = updaters.get(request.type());
		if (updater == null) {
			throw new IllegalStateException("Не найден updater для типа " + request.type());
		}
		updater.update(satellite, request);

		satellite = satelliteEntityService.save(satellite);
		log.info("Обновлен спутник: {}", satellite.getName());
		return mapper.toResponse(satellite);
	}

	@Transactional
	public void delete(Long id) {
		satelliteEntityService.delete(id);
	}

	public SatelliteResponse activate(Long id) {
		Satellite satellite = satelliteEntityService.findById(id);
		if (satellite.getBatteryLevel().compareTo(MIN_BATTERY_FOR_ACTIVATION) > 0 && !satellite.getActive()) {
			satellite.setActive(true);
			satellite = satelliteEntityService.save(satellite);
			log.info("✅ {}: Активация успешна", satellite.getName());
		} else {
			log.warn("🛑 {}: Ошибка активации (заряд: {}%)",
					satellite.getName(), satellite.getBatteryLevel().multiply(BigDecimal.valueOf(100)).intValue());
		}
		return mapper.toResponse(satellite);
	}

	@Transactional
	public SatelliteResponse deactivate(Long id) {
		Satellite satellite = satelliteEntityService.findById(id);
		satellite.setActive(false);
		log.info("Спутник {} деактивирован", satellite.getName());
		return mapper.toResponse(satellite);
	}

	@Transactional
	public SatelliteResponse performMission(Long id) {
		Satellite satellite = satelliteEntityService.findById(id);
		satellite.performMission();
		return mapper.toResponse(satellite);
	}

	@Transactional(readOnly = true)
	public List<SatelliteResponse> findByConstellationId(Long constellationId) {
		return satelliteEntityService.findByConstellationId(constellationId)
				.stream()
				.map(mapper::toResponse)
				.toList();
	}

	@Override
	public Satellite createEntity(SatelliteRequest request) {
		SatelliteCreator creator = creators.get(request.type());
		if (creator == null) {
			throw new IllegalArgumentException("Неизвестный тип спутника: " + request.type());
		}

		return creator.create(request);
	}
}
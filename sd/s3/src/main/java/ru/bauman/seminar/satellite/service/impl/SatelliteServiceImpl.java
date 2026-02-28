package ru.bauman.seminar.satellite.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.creator.SatelliteFactory;
import ru.bauman.seminar.satellite.entity.EnergySystem;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.entity.SatelliteState;
import ru.bauman.seminar.satellite.entity.SatelliteType;
import ru.bauman.seminar.satellite.mapper.SatelliteMapper;
import ru.bauman.seminar.satellite.service.SatelliteService;
import ru.bauman.seminar.satellite.service.entity.SatelliteEntityService;
import ru.bauman.seminar.satellite.updater.SatelliteUpdater;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SatelliteServiceImpl implements SatelliteService {

	private final SatelliteEntityService satelliteEntityService;
	private final SatelliteFactory satelliteFactory;
	private final Map<SatelliteType, SatelliteUpdater> updaters;
	private final SatelliteMapper mapper;

	public SatelliteServiceImpl(
			SatelliteEntityService satelliteEntityService,
			SatelliteFactory satelliteFactory,
			List<SatelliteUpdater> updaterList,
			SatelliteMapper mapper) {
		this.satelliteEntityService = satelliteEntityService;
		this.satelliteFactory = satelliteFactory;
		this.updaters = updaterList.stream()
				.collect(Collectors.toMap(SatelliteUpdater::getType, Function.identity()));
		this.mapper = mapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<SatelliteResponse> findAll() {
		return satelliteEntityService.findAll().stream()
				.map(mapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SatelliteResponse findById(Long id) {
		return mapper.toResponse(satelliteEntityService.findById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public SatelliteResponse findByName(String name) {
		return mapper.toResponse(satelliteEntityService.findByName(name));
	}

	@Override
	@Transactional
	public SatelliteResponse create(SatelliteRequest request) {
		Satellite satellite = satelliteFactory.createSatellite(request);
		satellite = satelliteEntityService.save(satellite);
		log.info("Создан спутник: {} типа {}", satellite.getName(), satellite.getType());
		return mapper.toResponse(satellite);
	}

	@Override
	@Transactional
	public SatelliteResponse update(Long id, SatelliteRequest request) {
		Satellite satellite = satelliteEntityService.findById(id);

		if (!satellite.getType().equals(request.type())) {
			throw new IllegalArgumentException("Нельзя изменить тип спутника");
		}

		satellite.setName(request.name());

		EnergySystem oldEnergy = satellite.getEnergySystem();
		EnergySystem newEnergy = EnergySystem.builder()
				.batteryLevel(request.batteryLevel())
				.minBattery(oldEnergy.getMinBattery())
				.maxBattery(oldEnergy.getMaxBattery())
				.lowBatteryThreshold(oldEnergy.getLowBatteryThreshold())
				.build();
		satellite.setEnergySystem(newEnergy);

		SatelliteUpdater updater = updaters.get(request.type());
		if (updater == null) {
			throw new IllegalStateException("Не найден updater для типа " + request.type());
		}
		updater.update(satellite, request);

		satellite = satelliteEntityService.save(satellite);
		log.info("Обновлен спутник: {}", satellite.getName());
		return mapper.toResponse(satellite);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		satelliteEntityService.delete(id);
	}

	@Override
	@Transactional
	public SatelliteResponse activate(Long id) {
		Satellite satellite = satelliteEntityService.findById(id);
		boolean activated = satellite.activate();
		if (activated) {
			satellite = satelliteEntityService.save(satellite);
			log.info("✅ {}: Активация успешна", satellite.getName());
		} else {
			log.warn("⚠️ {}: Не удалось активировать (состояние: {}, заряд: {}%)",
					satellite.getName(),
					satellite.getState(),
					satellite.getBatteryLevel().multiply(BigDecimal.valueOf(100)).intValue());
		}
		return mapper.toResponse(satellite);
	}

	@Override
	@Transactional
	public SatelliteResponse deactivate(Long id) {
		Satellite satellite = satelliteEntityService.findById(id);
		boolean deactivated = satellite.deactivate();
		if (deactivated) {
			satellite = satelliteEntityService.save(satellite);
			log.info("⏸️ {}: Деактивирован", satellite.getName());
		} else {
			log.debug("{}: уже неактивен", satellite.getName());
		}
		return mapper.toResponse(satellite);
	}

	@Override
	@Transactional
	public SatelliteResponse performMission(Long id) {
		Satellite satellite = satelliteEntityService.findById(id);
		satellite.performMission();
		satelliteEntityService.save(satellite);
		return mapper.toResponse(satellite);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SatelliteResponse> findByConstellationId(Long constellationId) {
		return satelliteEntityService.findByConstellationId(constellationId)
				.stream()
				.map(mapper::toResponse)
				.toList();
	}

	@Override
	public Satellite createEntity(SatelliteRequest request) {
		return satelliteFactory.createSatellite(request);
	}
}
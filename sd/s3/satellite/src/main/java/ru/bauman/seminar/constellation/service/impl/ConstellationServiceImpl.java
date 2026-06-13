package ru.bauman.seminar.constellation.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.common.aop.MeasureExecutionTime;
import ru.bauman.seminar.common.exception.EntityNotFoundException;
import ru.bauman.seminar.constellation.controller.dto.request.ConstellationRequest;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationResponse;
import ru.bauman.seminar.constellation.controller.dto.response.ConstellationStatusDto;
import ru.bauman.seminar.constellation.creator.ConstellationFactory;
import ru.bauman.seminar.constellation.entity.Constellation;
import ru.bauman.seminar.constellation.mapper.ConstellationMapper;
import ru.bauman.seminar.constellation.mapper.ConstellationStatusMapper;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.constellation.service.entity.ConstellationEntityService;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.controller.dto.response.SatelliteResponse;
import ru.bauman.seminar.satellite.entity.OutboxMessage;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.repository.OutboxRepository;
import ru.bauman.seminar.satellite.service.SatelliteService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstellationServiceImpl implements ConstellationService {

    private final ConstellationEntityService constellationEntityService;
    private final ConstellationFactory constellationFactory;
    private final SatelliteService satelliteService;
    private final ConstellationMapper constellationMapper;
    private final ConstellationStatusMapper constellationStatusMapper;
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional(readOnly = true)
    @MeasureExecutionTime
    public List<ConstellationResponse> findAll() {
        return constellationMapper.toResponseList(
            constellationEntityService.findAllWithSatellites()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ConstellationResponse findById(Long id) {
        return constellationMapper.toResponse(
            constellationEntityService.findByIdWithSatellites(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ConstellationResponse findByName(String name) {
        return constellationMapper.toResponse(
            constellationEntityService.findByName(name)
        );
    }

    @Override
    @Transactional
    public ConstellationResponse create(ConstellationRequest request) {
        Constellation constellation = constellationFactory.createConstellation(
            request.name(),
            request.description()
        );
        return constellationMapper.toResponse(
            constellationEntityService.save(constellation)
        );
    }

    @Override
    @Transactional
    public ConstellationResponse update(Long id, ConstellationRequest request) {
        Constellation existing = constellationEntityService.findById(id);
        existing.setName(request.name());
        existing.setDescription(request.description());
        log.info("Обновлена группировка: {}", existing.getName());
        return constellationMapper.toResponse(
            constellationEntityService.save(existing)
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        constellationEntityService.delete(id);
        log.info("Удалена группировка с id: {}", id);
    }

    @Override
    @Transactional
    public ConstellationResponse addSatellite(
        Long constellationId,
        SatelliteRequest request
    ) {
        Constellation constellation =
            constellationEntityService.findByIdWithSatellites(constellationId);

        Satellite satellite = satelliteService.createEntity(request);
        satellite.setConstellation(constellation);
        constellation.getSatellites().add(satellite);
        constellation = constellationEntityService.save(constellation);

        // Outbox — атомарно в той же транзакции
        String payload =
            "{\"eventType\":\"CREATED\",\"satelliteId\":" +
            satellite.getId() +
            ",\"satelliteName\":\"" +
            satellite.getName() +
            "\"}";
        outboxRepository.save(
            OutboxMessage.builder()
                .aggregateId(satellite.getId())
                .eventType("CREATED")
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .build()
        );

        log.info(
            "Добавлен спутник {} в группировку {}",
            satellite.getName(),
            constellation.getName()
        );
        return constellationMapper.toResponse(constellation);
    }

    @Override
    @Transactional
    public ConstellationResponse removeSatellite(
        Long constellationId,
        Long satelliteId
    ) {
        Constellation constellation =
            constellationEntityService.findByIdWithSatellites(constellationId);
        Satellite satellite = constellation
            .getSatellites()
            .stream()
            .filter(s -> s.getId().equals(satelliteId))
            .findFirst()
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "Спутник с id " + satelliteId + " не найден в группировке"
                )
            );

        String payload =
            "{\"eventType\":\"DELETED\",\"satelliteId\":" +
            satellite.getId() +
            ",\"satelliteName\":\"" +
            satellite.getName() +
            "\"}";
        outboxRepository.save(
            OutboxMessage.builder()
                .aggregateId(satellite.getId())
                .eventType("DELETED")
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .build()
        );

        satellite.setConstellation(null);
        constellation.getSatellites().remove(satellite);
        constellationEntityService.save(constellation);
        satelliteService.delete(satelliteId);

        log.info(
            "Удален спутник {} из группировки {}",
            satellite.getName(),
            constellation.getName()
        );
        return constellationMapper.toResponse(constellation);
    }

    @Override
    @Transactional
    @MeasureExecutionTime(
        operationName = "Активация всех спутников группировки"
    )
    public List<SatelliteResponse> activateAllSatellites(Long constellationId) {
        Constellation constellation =
            constellationEntityService.findByIdWithSatellites(constellationId);
        log.info(
            "Активация всех спутников в группировке: {}",
            constellation.getName()
        );
        return constellation
            .getSatellites()
            .stream()
            .map(s -> satelliteService.activate(s.getId()))
            .toList();
    }

    @Override
    @Transactional
    public List<SatelliteResponse> executeAllMissions(Long constellationId) {
        Constellation constellation =
            constellationEntityService.findByIdWithSatellites(constellationId);
        log.info(
            "Выполнение всех миссий в группировке: {}",
            constellation.getName()
        );
        return constellation
            .getSatellites()
            .stream()
            .map(s -> satelliteService.performMission(s.getId()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SatelliteResponse> getSatellites(Long constellationId) {
        return satelliteService.findByConstellationId(constellationId);
    }

    @Override
    @Transactional(readOnly = true)
    public ConstellationStatusDto getConstellationStatus(
        String constellationName
    ) {
        return constellationStatusMapper.toDto(
            constellationEntityService.findByName(constellationName)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ConstellationStatusDto getConstellationStatus(Long id) {
        return constellationStatusMapper.toDto(
            constellationEntityService.findByIdWithSatellites(id)
        );
    }
}

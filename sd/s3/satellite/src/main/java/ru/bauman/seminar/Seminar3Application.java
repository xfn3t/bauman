package ru.bauman.seminar;

import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.bauman.seminar.constellation.service.ConstellationService;
import ru.bauman.seminar.operations.SpaceOperationCenterService;
import ru.bauman.seminar.operations.dto.*;
import ru.bauman.seminar.satellite.controller.dto.request.SatelliteRequest;
import ru.bauman.seminar.satellite.entity.SatelliteType;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Seminar3Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(
            Seminar3Application.class,
            args
        );

        ConstellationService constellationService = context.getBean(
            ConstellationService.class
        );
        SpaceOperationCenterService operationCenter = context.getBean(
            SpaceOperationCenterService.class
        );

        log.info("Очистка базы данных...");
        constellationService
            .findAll()
            .forEach(c -> constellationService.delete(c.id()));

        operationCenter.addSatellites(
            AddSatelliteRequest.builder()
                .constellationName("Орбита-1")
                .constellationDescription("Основная группировка связи")
                .satellites(
                    List.of(
                        new SatelliteRequest(
                            "Связь-1",
                            BigDecimal.valueOf(0.85),
                            SatelliteType.COMMUNICATION,
                            BigDecimal.valueOf(500.0),
                            null
                        ),
                        new SatelliteRequest(
                            "ДЗЗ-1",
                            BigDecimal.valueOf(0.92),
                            SatelliteType.IMAGING,
                            null,
                            BigDecimal.valueOf(2.5)
                        ),
                        new SatelliteRequest(
                            "ДЗЗ-2",
                            BigDecimal.valueOf(0.45),
                            SatelliteType.IMAGING,
                            null,
                            BigDecimal.valueOf(1.0)
                        )
                    )
                )
                .build()
        );

        operationCenter.addSatellites(
            AddSatelliteRequest.builder()
                .constellationName("Орбита-2")
                .constellationDescription("Резервная группировка")
                .satellites(
                    List.of(
                        new SatelliteRequest(
                            "Связь-2",
                            BigDecimal.valueOf(0.75),
                            SatelliteType.COMMUNICATION,
                            BigDecimal.valueOf(1000.0),
                            null
                        ),
                        new SatelliteRequest(
                            "ДЗЗ-3",
                            BigDecimal.valueOf(0.15),
                            SatelliteType.IMAGING,
                            null,
                            BigDecimal.valueOf(0.5)
                        )
                    )
                )
                .build()
        );

        MissionResult orbit1Result = operationCenter.activateAndExecuteAll(
            "Орбита-1"
        );
        orbit1Result
            .processedSatellites()
            .forEach(sat -> log.info("{}: активация успешна", sat.name()));

        SystemStatusDto systemStatus = operationCenter.getSystemStatus();
        systemStatus
            .constellationStatuses()
            .stream()
            .filter(s -> s.getName().equals("Орбита-1"))
            .findFirst()
            .ifPresent(s -> log.info(s.getName()));
    }
}

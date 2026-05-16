package ru.bauman.seminar.telemetry;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.bauman.seminar.satellite.entity.Satellite;
import ru.bauman.seminar.satellite.repository.SatelliteRepository;
import ru.bauman.telemetry.proto.TelemetryRequest;
import ru.bauman.telemetry.proto.TelemetryServiceGrpc;
import ru.bauman.telemetry.proto.TelemetryUpdate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryClientService {

    @GrpcClient("telemetry-service")
    private TelemetryServiceGrpc.TelemetryServiceStub telemetryStub;

    private final SatelliteRepository satelliteRepository;

    @PostConstruct
    public void startStreaming() {
        TelemetryRequest request = TelemetryRequest.newBuilder()
            .setSatelliteName("")
            .build();

        log.info("Starting telemetry stream from telemetry-service...");

        telemetryStub.streamTelemetry(
            request,
            new StreamObserver<TelemetryUpdate>() {
                @Override
                public void onNext(TelemetryUpdate update) {
                    try {
                        satelliteRepository
                            .findByName(update.getSatelliteName())
                            .ifPresent(satellite ->
                                updateSatelliteTelemetry(satellite, update)
                            );
                    } catch (Exception e) {
                        log.error(
                            "Error processing telemetry update for {}: {}",
                            update.getSatelliteName(),
                            e.getMessage()
                        );
                    }
                }

                @Override
                public void onError(Throwable t) {
                    log.error("Telemetry stream error: {}", t.getMessage());
                }

                @Override
                public void onCompleted() {
                    log.info("Telemetry stream completed");
                }
            }
        );

        log.info("Telemetry stream subscription active");
    }

    private void updateSatelliteTelemetry(
        Satellite satellite,
        TelemetryUpdate update
    ) {
        satellite.setInternalTemperature(update.getInternalTemperature());
        satellite.setExternalTemperature(update.getExternalTemperature());
        satelliteRepository.save(satellite);
        log.info(
            "{} получил телеметрию: внутр. {}°C / внеш. {}°C",
            satellite.getName(),
            update.getInternalTemperature(),
            update.getExternalTemperature()
        );
    }
}

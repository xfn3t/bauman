package ru.bauman.telemetry;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.bauman.telemetry.proto.TelemetryRequest;
import ru.bauman.telemetry.proto.TelemetryServiceGrpc;
import ru.bauman.telemetry.proto.TelemetryUpdate;

@Slf4j
@GrpcService
public class TelemetryServiceImpl extends TelemetryServiceGrpc.TelemetryServiceImplBase {

    private static final List<String> SATELLITES = List.of(
        "Связь-1",
        "Связь-2",
        "ДЗЗ-1",
        "ДЗЗ-2",
        "ДЗЗ-3"
    );

    private static final double INTERNAL_TEMP_BASE = 22.0;
    private static final double INTERNAL_TEMP_AMPLITUDE = 5.0;
    private static final double EXTERNAL_TEMP_MIN = -150.0;
    private static final double EXTERNAL_TEMP_MAX = 120.0;
    private static final int STREAM_INTERVAL_SEC = 2;

    private final Random random = new Random();

    @Override
    public void streamTelemetry(
        TelemetryRequest request,
        StreamObserver<TelemetryUpdate> responseObserver
    ) {
        String filter = request.getSatelliteName();
        log.info(
            "Клиент подключен, фильтр: '{}'",
            filter.isEmpty() ? "ВСЕ" : filter
        );

        List<String> targets = resolveTargets(filter);
        if (targets.isEmpty()) {
            log.warn("Спутник '{}' не найден - завершаем стрим", filter);
            responseObserver.onCompleted();
            return;
        }

        ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(
            () -> sendBatch(targets, responseObserver),
            0,
            STREAM_INTERVAL_SEC,
            TimeUnit.SECONDS
        );

        Context.current().addListener(
            context -> {
                log.info("Клиент отключился - стрим остановлен");
                task.cancel(true);
                scheduler.shutdown();
            },
            scheduler
        );
    }

    private List<String> resolveTargets(String filter) {
        if (filter.isEmpty()) return SATELLITES;
        return SATELLITES.stream()
            .filter(s -> s.equalsIgnoreCase(filter))
            .toList();
    }

    private void sendBatch(
        List<String> targets,
        StreamObserver<TelemetryUpdate> responseObserver
    ) {
        try {
            long now = System.currentTimeMillis();

            for (String name : targets) {
                double internal = generateInternalTemp();
                double external = generateExternalTemp();

                TelemetryUpdate update = TelemetryUpdate.newBuilder()
                    .setSatelliteName(name)
                    .setInternalTemperature(internal)
                    .setExternalTemperature(external)
                    .setTimestamp(now)
                    .build();
                responseObserver.onNext(update);

                log.info(
                    "{}: внутр. {}°C / внеш. {}°C",
                    name,
                    internal,
                    external
                );
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке телеметрии: {}", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }

    private double generateInternalTemp() {
        double raw =
            INTERNAL_TEMP_BASE +
            random.nextGaussian() * INTERNAL_TEMP_AMPLITUDE;
        return round2(raw);
    }

    private double generateExternalTemp() {
        double range = EXTERNAL_TEMP_MAX - EXTERNAL_TEMP_MIN;
        double raw = EXTERNAL_TEMP_MIN + random.nextDouble() * range;
        return round2(raw);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

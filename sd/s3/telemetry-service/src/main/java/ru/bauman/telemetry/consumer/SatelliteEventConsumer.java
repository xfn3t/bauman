package ru.bauman.telemetry.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.telemetry.entity.InboxMessage;
import ru.bauman.telemetry.repository.InboxRepository;
import ru.bauman.telemetry.service.SatelliteRegistry;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatelliteEventConsumer {

    private final InboxRepository inboxRepository;
    private final SatelliteRegistry registry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "satellite-events", groupId = "telemetry-group")
    @Transactional
    public void consume(String payload,
                        @Header(KafkaHeaders.RECEIVED_KEY) String key,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
                        @Header(KafkaHeaders.OFFSET) String offset) {
        String eventId = topic + "-" + partition + "-" + offset;

        if (inboxRepository.existsByEventId(eventId)) {
            log.info("Событие {} уже обработано, пропускаем", eventId);
            return;
        }

        try {
            JsonNode node = objectMapper.readTree(payload);
            String eventType = node.get("eventType").asText();
            long satelliteId = node.get("satelliteId").asLong();
            String satelliteName = node.get("satelliteName").asText();

            if ("CREATED".equals(eventType)) {
                registry.add(satelliteName);
                log.info("Добавлен спутник: {} (id={})", satelliteName, satelliteId);
            } else if ("DELETED".equals(eventType)) {
                registry.remove(satelliteName);
                log.info("Удален спутник: {} (id={})", satelliteName, satelliteId);
            }

            InboxMessage inbox = InboxMessage.builder()
                .eventId(eventId)
                .aggregateId(satelliteId)
                .eventType(eventType)
                .processedAt(LocalDateTime.now())
                .build();
            inboxRepository.save(inbox);
        } catch (Exception e) {
            log.error("Ошибка обработки события: {}", e.getMessage());
            throw new RuntimeException("Ошибка inbox", e);
        }
    }
}

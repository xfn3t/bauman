package ru.bauman.seminar.satellite.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bauman.seminar.satellite.entity.OutboxMessage;
import ru.bauman.seminar.satellite.repository.OutboxRepository;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "satellite-events";

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        List<OutboxMessage> pending = outboxRepository.findByStatusOrderByCreatedAtAsc("PENDING");
        for (OutboxMessage msg : pending) {
            try {
                kafkaTemplate.send(TOPIC, String.valueOf(msg.getAggregateId()), msg.getPayload()).get();
                msg.setStatus("SENT");
                outboxRepository.save(msg);
                log.info("Outbox отправлен: id={}, type={}", msg.getId(), msg.getEventType());
            } catch (Exception e) {
                log.error("Ошибка отправки outbox id={}: {}", msg.getId(), e.getMessage());
            }
        }
    }
}

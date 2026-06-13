package ru.bauman.telemetry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bauman.telemetry.entity.InboxMessage;

public interface InboxRepository extends JpaRepository<InboxMessage, Long> {
    boolean existsByEventId(String eventId);
}

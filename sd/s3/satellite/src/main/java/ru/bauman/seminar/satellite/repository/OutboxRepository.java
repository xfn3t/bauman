package ru.bauman.seminar.satellite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bauman.seminar.satellite.entity.OutboxMessage;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    List<OutboxMessage> findByStatusOrderByCreatedAtAsc(String status);
}

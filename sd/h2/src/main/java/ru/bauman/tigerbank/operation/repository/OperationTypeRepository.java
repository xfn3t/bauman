package ru.bauman.tigerbank.operation.repository;

import ru.bauman.tigerbank.operation.entity.OperationType;
import ru.bauman.tigerbank.operation.entity.OperationTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {
	Optional<OperationType> findByName(OperationTypeEnum name);
}
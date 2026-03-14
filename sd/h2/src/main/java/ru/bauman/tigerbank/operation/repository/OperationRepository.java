package ru.bauman.tigerbank.operation.repository;

import ru.bauman.tigerbank.operation.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {
	List<Operation> findByAccountIdAndDateBetween(Long accountId, LocalDateTime from, LocalDateTime to);
	List<Operation> findByCategoryId(Long categoryId);

	@Query("SELECT o FROM Operation o WHERE o.account.id = :accountId AND o.date BETWEEN :from AND :to AND o.type.id = :typeId")
	List<Operation> findByAccountAndDateAndType(@Param("accountId") Long accountId,
												@Param("from") LocalDateTime from,
												@Param("to") LocalDateTime to,
												@Param("typeId") Long typeId);

	List<Operation> findByAccountId(Long accountId);
}
package ru.bauman.tigerbank.operation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.bauman.tigerbank.account.entity.BankAccount;
import ru.bauman.tigerbank.category.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false)
	private LocalDateTime date;

	private String description;

	@ManyToOne
	@JoinColumn(name = "operation_type_id", nullable = false)
	private OperationType type;

	@ManyToOne
	@JoinColumn(name = "bank_account_id", nullable = false)
	private BankAccount account;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
}
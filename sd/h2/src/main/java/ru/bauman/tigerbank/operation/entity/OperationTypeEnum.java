package ru.bauman.tigerbank.operation.entity;

import java.math.BigDecimal;
import java.util.function.Function;

public enum OperationTypeEnum {
	INCOME(amount -> amount),
	EXPENSE(BigDecimal::negate);

	private final Function<BigDecimal, BigDecimal> effect;

	OperationTypeEnum(Function<BigDecimal, BigDecimal> effect) { this.effect = effect; }
	public BigDecimal applyEffect(BigDecimal amount) { return effect.apply(amount); }
}
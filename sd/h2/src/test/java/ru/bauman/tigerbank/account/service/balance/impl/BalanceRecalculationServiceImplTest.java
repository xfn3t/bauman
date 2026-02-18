package ru.bauman.tigerbank.account.service.balance.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.bauman.tigerbank.account.entity.BankAccount;
import ru.bauman.tigerbank.account.service.balance.BalanceCalculator;
import ru.bauman.tigerbank.account.service.entity.BankAccountEntityService;
import ru.bauman.tigerbank.operation.entity.Operation;
import ru.bauman.tigerbank.operation.service.entity.OperationEntityService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BalanceRecalculationServiceImplTest {

	@Mock
	private BankAccountEntityService accountEntityService;

	@Mock
	private OperationEntityService operationEntityService;

	@Mock
	private BalanceCalculator balanceCalculator;

	@InjectMocks
	private BalanceRecalculationServiceImpl recalculationService;

	@Test
	void autoRecalc_ShouldCalculateAndSetBalance() {
		Long accountId = 1L;
		BankAccount account = BankAccount.builder().id(accountId).balance(BigDecimal.ZERO).build();
		List<Operation> operations = List.of(mock(Operation.class), mock(Operation.class));
		BigDecimal calculatedBalance = BigDecimal.valueOf(1500);

		when(accountEntityService.getById(accountId)).thenReturn(account);
		when(operationEntityService.findAllByAccountId(accountId)).thenReturn(operations);
		when(balanceCalculator.calculate(operations)).thenReturn(calculatedBalance);

		recalculationService.autoRecalc(accountId);

		verify(account).setBalance(calculatedBalance);
		verify(accountEntityService).save(account);
	}

	@Test
	void manualRecalc_ShouldCorrectBalanceIfDifferent() {
		Long accountId = 1L;
		BankAccount account = BankAccount.builder().id(accountId).balance(BigDecimal.valueOf(1000)).build();
		List<Operation> operations = List.of();
		BigDecimal autoBalance = BigDecimal.valueOf(1000);
		BigDecimal correctBalance = BigDecimal.valueOf(1200); // отличается

		when(accountEntityService.getById(accountId)).thenReturn(account);
		when(operationEntityService.findAllByAccountId(accountId)).thenReturn(operations);
		when(balanceCalculator.calculate(operations)).thenReturn(autoBalance);

		recalculationService.manualRecalc(accountId, correctBalance);

		verify(account).setBalance(correctBalance);
		verify(accountEntityService, times(2)).save(account);
	}

	@Test
	void manualRecalc_ShouldNotCorrectIfBalancesMatch() {
		Long accountId = 1L;
		BankAccount account = BankAccount.builder().id(accountId).balance(BigDecimal.valueOf(1000)).build();
		List<Operation> operations = List.of();
		BigDecimal autoBalance = BigDecimal.valueOf(1000);
		BigDecimal correctBalance = BigDecimal.valueOf(1000); // совпадает

		when(accountEntityService.getById(accountId)).thenReturn(account);
		when(operationEntityService.findAllByAccountId(accountId)).thenReturn(operations);
		when(balanceCalculator.calculate(operations)).thenReturn(autoBalance);

		recalculationService.manualRecalc(accountId, correctBalance);

		verify(accountEntityService, times(1)).save(account);
		verify(account, times(1)).setBalance(autoBalance); // только из autoRecalc
	}
}
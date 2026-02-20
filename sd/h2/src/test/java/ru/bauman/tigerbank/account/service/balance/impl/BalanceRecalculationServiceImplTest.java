package ru.bauman.tigerbank.account.service.balance.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@Captor
	private ArgumentCaptor<BankAccount> accountCaptor;

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

		verify(accountEntityService).save(accountCaptor.capture());
		BankAccount savedAccount = accountCaptor.getValue();
		assertEquals(calculatedBalance, savedAccount.getBalance());
	}

	@Test
	void manualRecalc_ShouldCorrectBalanceIfDifferent() {
		Long accountId = 1L;
		BankAccount initialAccount = BankAccount.builder()
				.id(accountId)
				.balance(BigDecimal.valueOf(1000))
				.build();

		AtomicReference<BankAccount> currentAccount = new AtomicReference<>(initialAccount);

		when(accountEntityService.getById(accountId)).thenAnswer(inv -> {
			BankAccount state = currentAccount.get();
			return BankAccount.builder()
					.id(state.getId())
					.balance(state.getBalance())
					.build();
		});

		List<BankAccount> savedSnapshots = new ArrayList<>();
		doAnswer(inv -> {
			BankAccount argument = inv.getArgument(0);
			BankAccount snapshot = BankAccount.builder()
					.id(argument.getId())
					.balance(argument.getBalance())
					.build();
			savedSnapshots.add(snapshot);
			currentAccount.set(snapshot);
			return null;
		}).when(accountEntityService).save(any(BankAccount.class));

		List<Operation> operations = List.of();
		BigDecimal autoBalance = BigDecimal.valueOf(1000);
		BigDecimal correctBalance = BigDecimal.valueOf(1200);

		when(operationEntityService.findAllByAccountId(accountId)).thenReturn(operations);
		when(balanceCalculator.calculate(operations)).thenReturn(autoBalance);

		recalculationService.manualRecalc(accountId, correctBalance);

		assertEquals(2, savedSnapshots.size());
		assertEquals(autoBalance, savedSnapshots.get(0).getBalance());
		assertEquals(correctBalance, savedSnapshots.get(1).getBalance());
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

		verify(accountEntityService, times(1)).save(accountCaptor.capture());
		BankAccount savedAccount = accountCaptor.getValue();
		assertEquals(autoBalance, savedAccount.getBalance()); // autoBalance == correctBalance
	}
}
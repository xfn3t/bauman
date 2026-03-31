package ru.bauman.tigerbank.account.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.entity.BankAccount;
import ru.bauman.tigerbank.account.mapper.BankAccountMapper;
import ru.bauman.tigerbank.account.service.entity.BankAccountEntityService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

	@Mock
	private BankAccountEntityService entityService;

	@Mock
	private BankAccountMapper mapper;

	@InjectMocks
	private BankAccountServiceImpl accountService;

	private BankAccount accountEntity;
	private BankAccountDto accountDto;

	@BeforeEach
	void setUp() {
		accountEntity = BankAccount.builder()
				.id(1L)
				.name("Основной счет")
				.balance(BigDecimal.valueOf(1000))
				.build();

		accountDto = new BankAccountDto(1L, "Основной счет", BigDecimal.valueOf(1000));
	}

	@Test
	void create_ShouldMapDtoToEntityAndSave() {
		BankAccountDto inputDto = new BankAccountDto(null, "Новый счет", BigDecimal.ZERO);
		BankAccount inputEntity = BankAccount.builder().name("Новый счет").balance(BigDecimal.ZERO).build();
		BankAccount savedEntity = BankAccount.builder().id(2L).name("Новый счет").balance(BigDecimal.ZERO).build();
		BankAccountDto expectedDto = new BankAccountDto(2L, "Новый счет", BigDecimal.ZERO);

		when(mapper.toEntity(inputDto)).thenReturn(inputEntity);
		when(entityService.save(inputEntity)).thenReturn(savedEntity);
		when(mapper.toDto(savedEntity)).thenReturn(expectedDto);

		BankAccountDto result = accountService.create(inputDto);

		assertThat(result).isEqualTo(expectedDto);
		verify(mapper).toEntity(inputDto);
		verify(entityService).save(inputEntity);
		verify(mapper).toDto(savedEntity);
	}

	@Test
	void update_ShouldUpdateExistingEntity() {
		Long id = 1L;
		BankAccountDto updateDto = new BankAccountDto(id, "Обновленный счет", BigDecimal.valueOf(2000));
		BankAccount existing = BankAccount.builder().id(id).name("Старый счет").balance(BigDecimal.valueOf(1000)).build();

		when(entityService.getById(id)).thenReturn(existing);
		when(entityService.save(existing)).thenReturn(existing);
		when(mapper.toDto(existing)).thenReturn(updateDto);

		BankAccountDto result = accountService.update(id, updateDto);

		assertThat(result).isEqualTo(updateDto);
		assertThat(existing.getName()).isEqualTo("Обновленный счет");
		assertThat(existing.getBalance()).isEqualByComparingTo("2000");
		verify(entityService).save(existing);
	}

	@Test
	void delete_ShouldCallEntityServiceDelete() {
		Long id = 1L;
		doNothing().when(entityService).deleteById(id);

		accountService.delete(id);

		verify(entityService).deleteById(id);
	}

	@Test
	void findById_ShouldReturnMappedDto() {
		Long id = 1L;
		when(entityService.getById(id)).thenReturn(accountEntity);
		when(mapper.toDto(accountEntity)).thenReturn(accountDto);

		BankAccountDto result = accountService.findById(id);

		assertThat(result).isEqualTo(accountDto);
	}

	@Test
	void findAll_ShouldReturnListOfDtos() {
		List<BankAccount> entities = List.of(accountEntity);
		List<BankAccountDto> dtos = List.of(accountDto);

		when(entityService.getAll()).thenReturn(entities);
		when(mapper.toDtoList(entities)).thenReturn(dtos);

		List<BankAccountDto> result = accountService.findAll();

		assertThat(result).hasSize(1).containsExactly(accountDto);
	}
}
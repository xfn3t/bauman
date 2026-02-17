package ru.bauman.tigerbank.account.mapper;

import ru.bauman.tigerbank.account.dto.BankAccountDto;
import ru.bauman.tigerbank.account.entity.BankAccount;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
	BankAccountDto toDto(BankAccount entity);
	BankAccount toEntity(BankAccountDto dto);
	List<BankAccountDto> toDtoList(List<BankAccount> entities);
}
package ru.bauman.tigerbank.operation.mapper;

import ru.bauman.tigerbank.account.mapper.BankAccountMapper;
import ru.bauman.tigerbank.category.mapper.CategoryMapper;
import ru.bauman.tigerbank.operation.dto.OperationDto;
import ru.bauman.tigerbank.operation.entity.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BankAccountMapper.class, CategoryMapper.class, OperationTypeMapper.class})
public interface OperationMapper {
	@Mapping(source = "type", target = "type")
	@Mapping(source = "account", target = "account")
	@Mapping(source = "category", target = "category")
	OperationDto toDto(Operation entity);

	@Mapping(source = "type", target = "type")
	@Mapping(source = "account", target = "account")
	@Mapping(source = "category", target = "category")
	Operation toEntity(OperationDto dto);

	List<OperationDto> toDtoList(List<Operation> entities);
}
package ru.bauman.tigerbank.operation.mapper;

import ru.bauman.tigerbank.operation.dto.OperationTypeDto;
import ru.bauman.tigerbank.operation.entity.OperationType;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationTypeMapper {
	OperationTypeDto toDto(OperationType entity);
	OperationType toEntity(OperationTypeDto dto);
	List<OperationTypeDto> toDtoList(List<OperationType> entities);
}
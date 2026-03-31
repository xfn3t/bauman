package ru.bauman.tigerbank.category.mapper;

import ru.bauman.tigerbank.category.dto.CategoryTypeDto;
import ru.bauman.tigerbank.category.entity.CategoryType;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryTypeMapper {
	CategoryTypeDto toDto(CategoryType entity);
	CategoryType toEntity(CategoryTypeDto dto);
	List<CategoryTypeDto> toDtoList(List<CategoryType> entities);
}
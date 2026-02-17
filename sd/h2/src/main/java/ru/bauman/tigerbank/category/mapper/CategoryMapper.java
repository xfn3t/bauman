package ru.bauman.tigerbank.category.mapper;

import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryTypeMapper.class})
public interface CategoryMapper {
	@Mapping(source = "type", target = "type")
	CategoryDto toDto(Category entity);

	@Mapping(source = "type", target = "type")
	Category toEntity(CategoryDto dto);

	List<CategoryDto> toDtoList(List<Category> entities);
}
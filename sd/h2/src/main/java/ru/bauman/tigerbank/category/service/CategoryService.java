package ru.bauman.tigerbank.category.service;

import ru.bauman.tigerbank.category.dto.CategoryDto;
import java.util.List;

public interface CategoryService {
	CategoryDto create(CategoryDto dto);
	CategoryDto update(Long id, CategoryDto dto);
	void delete(Long id);
	CategoryDto findById(Long id);
	List<CategoryDto> findAll();
}
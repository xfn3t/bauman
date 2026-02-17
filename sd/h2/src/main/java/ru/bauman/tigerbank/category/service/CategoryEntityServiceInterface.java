package ru.bauman.tigerbank.category.service;

import ru.bauman.tigerbank.category.entity.Category;
import java.util.List;

public interface CategoryEntityServiceInterface {
	Category save(Category category);
	void deleteById(Long id);
	Category getById(Long id);
	List<Category> getAll();
}
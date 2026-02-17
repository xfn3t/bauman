package ru.bauman.tigerbank.category.service.entity;

import ru.bauman.tigerbank.category.entity.Category;
import java.util.List;

public interface CategoryEntityService {
	Category save(Category category);
	void deleteById(Long id);
	Category getById(Long id);
	List<Category> getAll();
}
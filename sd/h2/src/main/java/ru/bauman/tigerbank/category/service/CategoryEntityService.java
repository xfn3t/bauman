package ru.bauman.tigerbank.category.service;

import ru.bauman.tigerbank.category.entity.Category;
import ru.bauman.tigerbank.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryEntityService implements CategoryEntityServiceInterface {
	private final CategoryRepository repository;

	@Override
	@Transactional
	public Category save(Category category) {
		return repository.save(category);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	@Override
	public Category getById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
	}

	@Override
	public List<Category> getAll() {
		return repository.findAll();
	}
}
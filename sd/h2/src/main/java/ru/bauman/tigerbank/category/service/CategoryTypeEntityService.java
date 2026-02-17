package ru.bauman.tigerbank.category.service;

import ru.bauman.tigerbank.category.entity.CategoryType;
import ru.bauman.tigerbank.category.entity.CategoryTypeEnum;
import ru.bauman.tigerbank.category.repository.CategoryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryTypeEntityService implements CategoryTypeEntityServiceInterface {
	private final CategoryTypeRepository repository;

	@Override
	public CategoryType getById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("CategoryType not found with id: " + id));
	}

	@Override
	public CategoryType getByName(CategoryTypeEnum name) {
		return repository.findByName(name)
				.orElseThrow(() -> new RuntimeException("CategoryType not found with name: " + name));
	}
}
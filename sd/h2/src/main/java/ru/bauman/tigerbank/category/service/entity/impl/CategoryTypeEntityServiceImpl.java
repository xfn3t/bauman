package ru.bauman.tigerbank.category.service.entity.impl;

import ru.bauman.tigerbank.category.entity.CategoryType;
import ru.bauman.tigerbank.category.entity.CategoryTypeEnum;
import ru.bauman.tigerbank.category.repository.CategoryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bauman.tigerbank.category.service.entity.CategoryTypeEntityService;

@Service
@RequiredArgsConstructor
public class CategoryTypeEntityServiceImpl implements CategoryTypeEntityService {
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
package ru.bauman.tigerbank.category.service;

import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.mapper.CategoryMapper;
import ru.bauman.tigerbank.category.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceInterface {
	private final CategoryEntityServiceInterface entityService;
	private final CategoryTypeEntityServiceInterface typeEntityService;
	private final CategoryMapper mapper;

	@Override
	@Transactional
	public CategoryDto create(CategoryDto dto) {
		Category entity = mapper.toEntity(dto);
		entity.setId(null);
		if (dto.type() != null && dto.type().id() != null) {
			entity.setType(typeEntityService.getById(dto.type().id()));
		} else {
			throw new RuntimeException("Category type is required");
		}
		Category saved = entityService.save(entity);
		return mapper.toDto(saved);
	}

	@Override
	@Transactional
	public CategoryDto update(Long id, CategoryDto dto) {
		Category existing = entityService.getById(id);
		existing.setName(dto.name());
		if (dto.type() != null && dto.type().id() != null) {
			existing.setType(typeEntityService.getById(dto.type().id()));
		}
		return mapper.toDto(entityService.save(existing));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		entityService.deleteById(id);
	}

	@Override
	public CategoryDto findById(Long id) {
		return mapper.toDto(entityService.getById(id));
	}

	@Override
	public List<CategoryDto> findAll() {
		return mapper.toDtoList(entityService.getAll());
	}
}
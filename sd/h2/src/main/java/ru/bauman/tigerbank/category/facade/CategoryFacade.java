package ru.bauman.tigerbank.category.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bauman.tigerbank.category.dto.CategoryDto;
import ru.bauman.tigerbank.category.factory.CategoryFactory;
import ru.bauman.tigerbank.category.service.CategoryService;
import ru.bauman.tigerbank.category.dto.CreateCategoryRequest;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryFacade {

    private final CategoryService categoryService;
    private final CategoryFactory categoryFactory;

    public CategoryDto create(CreateCategoryRequest request) {
        return categoryService.create(categoryFactory.create(request));
    }

    public CategoryDto update(Long id, CategoryDto dto) {
        return categoryService.update(id, dto);
    }

    public void delete(Long id) {
        categoryService.delete(id);
    }

    public CategoryDto findById(Long id) {
        return categoryService.findById(id);
    }

    public List<CategoryDto> findAll() {
        return categoryService.findAll();
    }
}
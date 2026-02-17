package ru.bauman.tigerbank.category.repository;

import ru.bauman.tigerbank.category.entity.CategoryType;
import ru.bauman.tigerbank.category.entity.CategoryTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryTypeRepository extends JpaRepository<CategoryType, Long> {
	Optional<CategoryType> findByName(CategoryTypeEnum name);
}
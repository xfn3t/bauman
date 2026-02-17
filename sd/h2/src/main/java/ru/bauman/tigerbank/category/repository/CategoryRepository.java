package ru.bauman.tigerbank.category.repository;

import ru.bauman.tigerbank.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
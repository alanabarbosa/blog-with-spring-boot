package io.github.alanabarbosa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {}

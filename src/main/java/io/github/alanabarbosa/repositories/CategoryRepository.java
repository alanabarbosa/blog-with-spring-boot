package io.github.alanabarbosa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	//@Query("SELECT c FROM Category c LEFT JOIN FETCH c.posts WHERE c.id = :id")
	//Optional<Category> findByIdWithPosts(@Param("id") Long id);
}

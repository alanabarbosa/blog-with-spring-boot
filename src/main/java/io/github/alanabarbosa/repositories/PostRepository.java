package io.github.alanabarbosa.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
	@Query("SELECT p FROM Post p WHERE p.category.id = :categoryId") 
	List<Post> findByCategoryId(Long categoryId);
	
	@Modifying
	@Query("UPDATE Post p SET p.status = false WHERE p.id =:id")
	void disablePost(@Param("id") Long id);
	
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    List<Post> findPostsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    Page<Post> findPostsByUserIdPage(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId") 
    Page<Post> findByCategoryIdPage(@Param("categoryId") Long categoryId, Pageable pageable);
}

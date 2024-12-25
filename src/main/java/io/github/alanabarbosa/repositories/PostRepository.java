package io.github.alanabarbosa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
	@Query("SELECT p FROM Post p WHERE p.category.id = :categoryId") 
	List<Post> findByCategoryId(Long categoryId);
	
	@Modifying
	@Query("UPDATE Post p SET p.status = false WHERE p.id =:id")
	void disablePost(@Param("id") Long id);
    
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    List<Post> findPostsByUserId(@Param("userId") Long userId);
    
   // @Query("SELECT p FROM Post p WHERE p.category.id = :userId")
    //List<Post> findPostsByCategoryId(@Param("userId") Long userId);    
}

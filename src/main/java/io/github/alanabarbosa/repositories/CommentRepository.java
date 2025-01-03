package io.github.alanabarbosa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("SELECT c FROM Comment c " +
		       "LEFT JOIN FETCH c.post p " +
		       "LEFT JOIN FETCH p.user pu " + 
		       "LEFT JOIN FETCH p.category pc " +
		       "LEFT JOIN FETCH c.user u " +
		       "WHERE c.id = :id")
    Optional<Comment> findByIdWithRelations(Long id);
    
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findByPostId(Long postId);
    
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.user")
    List<Comment> findAllWithUser();
    
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findCommentsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Comment p WHERE p.post.id = :postId")
    List<Comment> findCommentsByPostId(@Param("postId") Long postId);
    
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    Page<Comment> findCommentsByUserIdPage(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT p FROM Comment p WHERE p.post.id = :postId")
    Page<Comment> findCommentsByPostIdPage(@Param("postId") Long postId, Pageable pageable);    
    
}

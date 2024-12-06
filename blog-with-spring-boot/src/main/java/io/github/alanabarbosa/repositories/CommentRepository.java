package io.github.alanabarbosa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.github.alanabarbosa.model.Comment;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.post p " +
           "LEFT JOIN FETCH p.user pu " + 
           "LEFT JOIN FETCH p.category pc " +
           "LEFT JOIN FETCH c.user u " +
           "WHERE c.id = :id")
    Optional<Comment> findByIdWithRelations(Long id);
}

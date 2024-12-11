package io.github.alanabarbosa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}

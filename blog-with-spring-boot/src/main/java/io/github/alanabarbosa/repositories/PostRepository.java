package io.github.alanabarbosa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +  // Carregar os comentários junto com o post
            "LEFT JOIN FETCH p.user u " + 
            "LEFT JOIN FETCH p.category pc " +
            "WHERE p.id = :postId")
     Optional<Post> findByIdWithComments(Long postId);
     
     // Adicione a consulta `findAll` com `JOIN FETCH` se necessário
     @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.comments c " +
            "LEFT JOIN FETCH p.user u " + 
            "LEFT JOIN FETCH p.category pc")
     List<Post> findAllWithComments();

}

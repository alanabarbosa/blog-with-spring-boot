package io.github.alanabarbosa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {}

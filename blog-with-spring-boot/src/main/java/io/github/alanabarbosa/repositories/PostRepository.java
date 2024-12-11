package io.github.alanabarbosa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.alanabarbosa.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


}

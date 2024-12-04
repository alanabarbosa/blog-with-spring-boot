package io.github.alanabarbosa.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.repositories.PostRepository;

@Service
public class PostServices {
	
	private Logger logger = Logger.getLogger(PostServices.class.getName());
	
	@Autowired
	PostRepository repository;
	
	public List<Post> findAll() {
		
		logger.info("Finding all posts!");
		
		return repository.findAll();
	}
	
	public Post findById(Long id) {
		
		logger.info("Finding one post!");
		
	    return repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
	}
	
	public Post create(Post post) {
		
		logger.info("Creating one post!");
		
		return repository.save(post);
	}

	
	public Post update(Post post) {
		
		logger.info("Updating one post!");		

		var entity =  repository.findById(post.getId())
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		entity.setTitle(post.getTitle());
		entity.setContent(post.getContent());
		entity.setAuthorId(post.getAuthorId());
		entity.setSlug(post.getSlug());
		entity.setCreatedAt(post.getCreatedAt());
		entity.setUpdatedAt(post.getUpdatedAt());
		entity.setPublishedAt(post.getPublishedAt());	    
		entity.setStatus(post.getStatus());
		entity.setCategoriesId(post.getCategoriesId());
		entity.setImageId(post.getImageId());	    
	    
		return repository.save(entity);
	}
	
	public void delete(Long id) {
		logger.info("Deleting one post!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}

}

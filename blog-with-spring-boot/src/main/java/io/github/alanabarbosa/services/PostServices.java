package io.github.alanabarbosa.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.repositories.PostRepository;

@Service
public class PostServices {
	
	private Logger logger = Logger.getLogger(PostServices.class.getName());
	
	@Autowired
	PostRepository repository;	
	
	public List<PostVO> findAll() {
		
		logger.info("Finding all posts!");
		
		return DozerMapper.parseListObjects(repository.findAll(), PostVO.class);
	}
	
	public PostVO findById(Long id) {
		
		logger.info("Finding one post!");
		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
	    
	    return DozerMapper.parseObject(entity, PostVO.class);
	}
	
	public PostVO create(PostVO post) {
		
		logger.info("Creating one post!");
		
		var entity = DozerMapper.parseObject(post, Post.class);
		
		var vo = DozerMapper.parseObject(repository.save(entity), PostVO.class);		
		return vo;
	}
	
	public PostVO update(PostVO post) {
		
		logger.info("Updating one post!");		

		var entity =  repository.findById(post.getId())
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		entity.setTitle(post.getTitle());
		entity.setContent(post.getContent());
		entity.setSlug(post.getSlug());
		entity.setCreatedAt(post.getCreatedAt());
		entity.setUpdatedAt(post.getUpdatedAt());
		entity.setPublishedAt(post.getPublishedAt());	    
		entity.setStatus(post.getStatus());
		entity.setUserId(post.getUserId());
		entity.setCategory(post.getCategory());
		entity.setImageDesktop(post.getImageDesktop());	
		entity.setImageMobile(post.getImageMobile());	
	    
		var vo = DozerMapper.parseObject(repository.save(entity), PostVO.class);		
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one post!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}

}

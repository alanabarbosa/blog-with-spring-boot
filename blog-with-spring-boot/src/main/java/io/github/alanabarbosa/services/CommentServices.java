package io.github.alanabarbosa.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.repositories.CommentRepository;

@Service
public class CommentServices {
	
	private Logger logger = Logger.getLogger(CommentServices.class.getName());
	
	@Autowired
	CommentRepository repository;	
	
	public List<CommentVO> findAll() {
		
		logger.info("Finding all categories!");
		
		return DozerMapper.parseListObjects(repository.findAll(), CommentVO.class);
	}
	
	public CommentVO findById(Long id) {
		
		logger.info("Finding one comment!");
		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
	    
	    return DozerMapper.parseObject(entity, CommentVO.class);
	}
	
	public CommentVO create(CommentVO comment) {
		
		logger.info("Creating one comment!");
		
		var entity = DozerMapper.parseObject(comment, Comment.class);
		
		var vo = DozerMapper.parseObject(repository.save(entity), CommentVO.class);		
		return vo;
	}
	
	public CommentVO update(CommentVO comment) {
		
		logger.info("Updating one comment!");		

		var entity =  repository.findById(comment.getId())
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		entity.setContent(comment.getContent());
		entity.setCreatedAt(comment.getCreatedAt());
		entity.setStatus(comment.getStatus());
		entity.setPost(comment.getPost());
		entity.setUser(comment.getUser());
	    
		var vo = DozerMapper.parseObject(repository.save(entity), CommentVO.class);		
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one comment!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}

}

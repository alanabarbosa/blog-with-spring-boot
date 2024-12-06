package io.github.alanabarbosa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.controllers.PostController;
import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.repositories.PostRepository;
import io.github.alanabarbosa.util.NormalizeSlug;

@Service
public class PostServices {
	
	private Logger logger = Logger.getLogger(PostServices.class.getName());
	
	@Autowired
	PostRepository repository;	
	
	public List<PostVO> findAll() {		
		logger.info("Finding all posts!");		
		var persons = DozerMapper.parseListObjects(repository.findAll(), PostVO.class);
		persons
			.stream()
			.forEach(p -> {
				try {
					p.add(linkTo(methodOn(PostController.class).findById(p.getKey())).withSelfRel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});		
		return persons;
	}
	
	public PostVO findById(Long id) throws Exception {		
		logger.info("Finding one post!");		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
	    var vo = DozerMapper.parseObject(entity, PostVO.class);
	    vo.add(linkTo(methodOn(PostController.class).findById(id)).withSelfRel());
	    return vo;
	}
	
	public PostVO create(PostVO post) throws Exception {
		
		if (post == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one post!");
		var entity = DozerMapper.parseObject(post, Post.class);
		
		
		if (entity.getStatus()) entity.setPublishedAt(LocalDateTime.now());
		else entity.setPublishedAt(null);
		
	    var slugFormatted = NormalizeSlug.normalizeString(entity.getTitle());	    
	    if (!slugFormatted.isEmpty()) entity.setSlug(slugFormatted);
		
		var savedPost = repository.save(entity);
		var vo =  DozerMapper.parseObject(repository.save(savedPost), PostVO.class);
	    vo.add(linkTo(methodOn(PostController.class).findById(vo.getKey())).withSelfRel());
	    return vo;
	}
	
	public PostVO update(PostVO post) throws Exception {
		
		if (post == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one post!");
		var entity =  repository.findById(post.getKey())
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		entity.setTitle(post.getTitle());
		entity.setContent(post.getContent());
		
	    var slugFormatted = NormalizeSlug.normalizeString(entity.getTitle());
	    if (!slugFormatted.isEmpty()) entity.setSlug(slugFormatted);
		//entity.setCreatedAt(post.getCreatedAt());
		entity.setUpdatedAt(post.getUpdatedAt());
		//entity.setPublishedAt(post.getPublishedAt());
		entity.setStatus(post.getStatus());
		entity.setUser(post.getUser());
		entity.setCategory(post.getCategory());
		entity.setImageDesktop(post.getImageDesktop());
		entity.setImageMobile(post.getImageMobile());
		
		var updatedPost = repository.save(entity);
		var vo =  DozerMapper.parseObject(repository.save(updatedPost), PostVO.class);
	    vo.add(linkTo(methodOn(PostController.class).findById(vo.getKey())).withSelfRel());
	    return vo;		
	}
	
	public void delete(Long id) {
		logger.info("Deleting one post!");
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}

}

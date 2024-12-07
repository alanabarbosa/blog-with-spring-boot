package io.github.alanabarbosa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.controllers.CategoryController;
import io.github.alanabarbosa.controllers.CommentController;
import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.repositories.CategoryRepository;

@Service
public class CategoryServices {
	
	private Logger logger = Logger.getLogger(CategoryServices.class.getName());
	
	@Autowired
	CategoryRepository repository;	
	
	public List<CategoryVO> findAll() {
		
		logger.info("Finding all categories!");
		
		var categories = DozerMapper.parseListObjects(repository.findAll(), CategoryVO.class);
		categories
			.stream()
			.forEach(c -> {
				try {
					c.add(linkTo(methodOn(CategoryController.class).findById(c.getKey())).withSelfRel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});		
		return categories;
	}
	
	public CategoryVO findById(Long id) throws Exception {
		
		logger.info("Finding one post!");
		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
	    
	    var vo = DozerMapper.parseObject(entity, CategoryVO.class);
	    vo.add(linkTo(methodOn(CategoryController.class).findById(id)).withSelfRel());
	    return vo;
	}
	
	public CategoryVO create(CategoryVO category) throws Exception {
		if (category == null) throw new RequiredObjectIsNullException();
		logger.info("Creating one category!");
		
		var entity = DozerMapper.parseObject(category, Category.class);
		
		var vo = DozerMapper.parseObject(repository.save(entity), CategoryVO.class);		
	    vo.add(linkTo(methodOn(CategoryController.class).findById(vo.getKey())).withSelfRel());
	    return vo;
	}
	
	public CategoryVO update(CategoryVO category) throws Exception {
		if (category == null) throw new RequiredObjectIsNullException();
		logger.info("Updating one category!");		

		var entity =  repository.findById(category.getKey())
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		entity.setName(category.getName());
		entity.setDescription(category.getDescription());
		entity.setCreatedAt(category.getCreatedAt());	    
	    
		var vo = DozerMapper.parseObject(repository.save(entity), CategoryVO.class);		
		vo.add(linkTo(methodOn(CategoryController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one category!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}

}

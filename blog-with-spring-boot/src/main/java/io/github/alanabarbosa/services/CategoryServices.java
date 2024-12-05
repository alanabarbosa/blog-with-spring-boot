package io.github.alanabarbosa.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.repositories.CategoryRepository;

@Service
public class CategoryServices {
	
	private Logger logger = Logger.getLogger(CategoryServices.class.getName());
	
	@Autowired
	CategoryRepository repository;	
	
	public List<CategoryVO> findAll() {
		
		logger.info("Finding all categories!");
		
		return DozerMapper.parseListObjects(repository.findAll(), CategoryVO.class);
	}
	
	public CategoryVO findById(Long id) {
		
		logger.info("Finding one post!");
		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
	    
	    return DozerMapper.parseObject(entity, CategoryVO.class);
	}
	
	public CategoryVO create(CategoryVO category) {
		
		logger.info("Creating one category!");
		
		var entity = DozerMapper.parseObject(category, Category.class);
		
		var vo = DozerMapper.parseObject(repository.save(entity), CategoryVO.class);		
		return vo;
	}
	
	public CategoryVO update(CategoryVO category) {
		
		logger.info("Updating one category!");		

		var entity =  repository.findById(category.getId())
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		entity.setName(category.getName());
		entity.setDescription(category.getDescription());
		entity.setCreatedAt(category.getCreatedAt());	    
	    
		var vo = DozerMapper.parseObject(repository.save(entity), CategoryVO.class);		
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one category!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}

}

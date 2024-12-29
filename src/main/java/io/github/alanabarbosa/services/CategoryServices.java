package io.github.alanabarbosa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.controllers.CategoryController;
import io.github.alanabarbosa.controllers.PostController;
import io.github.alanabarbosa.data.vo.v1.CategoryBasicVO;
import io.github.alanabarbosa.data.vo.v1.CategoryResponseVO;
import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.data.vo.v1.PostBasicVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.repositories.CategoryRepository;
import io.github.alanabarbosa.repositories.PostRepository;

@Service
public class CategoryServices {
	
	private Logger logger = Logger.getLogger(CategoryServices.class.getName());
	
	@Autowired
	CategoryRepository repository;
	
	@Autowired
	PostRepository postRepository;
	
	public List<CategoryBasicVO> findAll() {
		
		logger.info("Finding all categories!");
		
		var categories = DozerMapper.parseListObjects(repository.findAll(), CategoryBasicVO.class);
		categories
			.stream()
			.forEach(c -> {
				try {
					c.add(linkTo(methodOn(CategoryController.class).findById(c.getKey())).withRel("category-details"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});		
		return categories;
	}
	
	public CategoryResponseVO findById(Long id) throws Exception {
	    logger.info("Finding one category with id: " + id);
	    
	    var entity = repository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));
	    
	    var vo = DozerMapper.parseObject(entity, CategoryResponseVO.class);

	    try {
	        List<Post> posts = postRepository.findByCategoryId(id);
	        List<PostBasicVO> postsVOs = posts.stream()
                .map(post -> {
                    PostBasicVO postVO = new PostBasicVO();
                    postVO.setKey(post.getId());
                    postVO.setTitle(post.getTitle());
                    try {
                        postVO.add(linkTo(methodOn(PostController.class).findById(post.getId())).withRel("posts-details"));
                    } catch (Exception e) {
                        logger.severe("Error adding HATEOAS link: " + e.getMessage());
                    }
                    return postVO;
                })
                .collect(Collectors.toList());
	        
	        vo.setPosts(postsVOs);
	    } catch (Exception e) {
	        logger.log(Level.SEVERE, "Error while processing posts for category " + id, e);
	    }

	    vo.add(linkTo(methodOn(CategoryController.class).findById(id)).withRel("category-details"));
        /*if (!vo.getPosts().isEmpty()) {
        	vo.getPosts().forEach(post -> {
                try {
                	post.add(linkTo(methodOn(PostController.class)
                	.findById(post.getKey())).withRel("posts-details"));
                } catch (Exception e) {
                    logger.severe("Error adding HATEOAS link for post: " + e.getMessage());
                }
            });
        }*/	    
	    
	    return vo;
	}
	
	public CategoryVO create(CategoryVO category) throws Exception {
		logger.info("Creating one category!");	
		if (category == null) throw new RequiredObjectIsNullException();
		
        if (category.getCreatedAt() == null) {
        	category.setCreatedAt(LocalDateTime.now());
        }
        
		var entity = DozerMapper.parseObject(category, Category.class);
		logger.info("Category entity name after mapping: " + entity.getName());
		
		var vo = DozerMapper.parseObject(repository.save(entity), CategoryVO.class);		
	    vo.add(linkTo(methodOn(CategoryController.class).findById(vo.getKey())).withRel("category-details"));
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
		vo.add(linkTo(methodOn(CategoryController.class).findById(vo.getKey())).withRel("category-details"));
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one category!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}

}

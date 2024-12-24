package io.github.alanabarbosa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.controllers.CommentController;
import io.github.alanabarbosa.controllers.UserController;
import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.model.User;
import io.github.alanabarbosa.repositories.CommentRepository;
import jakarta.transaction.Transactional;

@Service
public class CommentServices {
	
	private Logger logger = Logger.getLogger(CommentServices.class.getName());
	
	@Autowired
	CommentRepository repository;	
	@Transactional
	public List<CommentVO> findAll() {		
		logger.info("Finding all comments!");		
		
		//var comments = DozerMapper.parseListObjects(repository.findAll(), CommentVO.class);
		var comments = DozerMapper.parseListObjects(repository.findAllWithUser(), CommentVO.class);
		
	    comments
        .stream()
        .forEach(c -> {
            try {
                c.add(linkTo(methodOn(CommentController.class).findById(c.getKey())).withSelfRel());
                
                if (c.getUser() != null && c.getUser().getKey() != null) {
                    c.getUser().add(linkTo(methodOn(UserController.class).findById(c.getUser().getKey())).withSelfRel());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
	    
		return comments;
	}
	
	@Transactional
	public CommentVO findById(Long id) throws Exception {		
		logger.info("Finding one comment!");
		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));	
	    
	    var vo = DozerMapper.parseObject(entity, CommentVO.class);	  
	    
	    vo.add(linkTo(methodOn(CommentController.class).findById(id)).withSelfRel());
	    return vo;
	}
	
    public List<CommentVO> findCommentsByUserId(Long userId) {
        var comments = repository.findCommentsByUserId(userId);
        return DozerMapper.parseListObjects(comments, CommentVO.class);
    }	
	
	public CommentVO create(CommentVO comment) throws Exception {
	    if (comment == null) throw new RequiredObjectIsNullException();
	    
	    var entity = DozerMapper.parseObject(comment, Comment.class);
	    
	    var savedEntity = repository.save(entity);
	    
	    var fullComment = repository.findByIdWithRelations(savedEntity.getId())
	        .orElseThrow(() -> new RuntimeException("Comment not found"));
	    
	    var vo = DozerMapper.parseObject(fullComment, CommentVO.class);	
	    
	    vo.add(linkTo(methodOn(CommentController.class).findById(vo.getKey())).withSelfRel());
	    return vo;
	}
	
	public CommentVO update(CommentVO comment) throws Exception {
		if (comment == null) throw new RequiredObjectIsNullException();
		logger.info("Updating one comment!");		

		var entity =  repository.findById(comment.getKey())
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		entity.setContent(comment.getContent());
		entity.setCreatedAt(comment.getCreatedAt());
		entity.setStatus(comment.getStatus());
		
		var post = DozerMapper.parseObject(comment.getPost(), Post.class);
		entity.setPost(post);
		
		var user = DozerMapper.parseObject(comment.getUser(), User.class);
		entity.setUser(user);
	    
		var vo = DozerMapper.parseObject(repository.save(entity), CommentVO.class);		
		vo.add(linkTo(methodOn(CommentController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one comment!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}

}

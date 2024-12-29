package io.github.alanabarbosa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.controllers.CommentController;
import io.github.alanabarbosa.controllers.PostController;
import io.github.alanabarbosa.controllers.UserController;
import io.github.alanabarbosa.data.vo.v1.CommentBasicVO;
import io.github.alanabarbosa.data.vo.v1.CommentResponseVO;
import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.data.vo.v1.PostBasicVO;
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
	
	@Autowired
	PagedResourcesAssembler<CommentBasicVO> assembler;  
	
	@Transactional
	public PagedModel<EntityModel<CommentBasicVO>> findAll(Pageable pageable) {		
		logger.info("Finding all comments!");		
		
        var commentPage = repository.findAll(pageable);
        
        var commentVosPage = commentPage.map(c -> DozerMapper.parseObject(c, CommentBasicVO.class));
        
        commentVosPage.map(comment -> {
        	try {
        		comment.add(linkTo(methodOn(CommentController.class)
						.findById(comment.getKey())).withRel("comment-details"));
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error while processing posts " + comment.getKey(), e);
			}
        	return comment;
        });
        
        Link link = linkTo(methodOn(CommentController.class).findAll(
        		pageable.getPageNumber(),
        		pageable.getPageSize(), 
        		"asc")).withSelfRel();
        
        return assembler.toModel(commentVosPage, link);
	    
	}
	
	@Transactional
	public CommentResponseVO findById(Long id) throws Exception {		
		logger.info("Finding one comment!");
		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));	
	    
	    var vo = DozerMapper.parseObject(entity, CommentResponseVO.class);	  
	    
	    vo.add(linkTo(methodOn(CommentController.class).findById(id)).withRel("comment-details"));
	    vo.getUser().add(linkTo(methodOn(UserController.class).findById(vo.getUser().getKey())).withRel("user-details"));	    
	   	 if (vo.getPost() != null) {
	   		vo.getPost().add(linkTo(methodOn(PostController.class).findById(vo.getPost().getKey())).withRel("post-details"));
	     }	    
	    return vo;
	}
	
	@Transactional
    public PagedModel<EntityModel<CommentBasicVO>> findCommentsByUserId(Long userId, Pageable pageable) {
		logger.info("Finding comments for user id!");  
		
		var commentsPage = repository.findCommentsByUserIdPage(userId, pageable);
		
		var commentsVosPage = commentsPage.map(c -> DozerMapper.parseObject(c, CommentBasicVO.class));

		commentsVosPage.map(comment -> {
            try {
                return comment.add(linkTo(methodOn(CommentController.class)
                        .findById(comment.getKey())).withRel("comment-details"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return comment;
        });
		
        Link link = null;
		try {
			link = linkTo(methodOn(CommentController.class)
			        .findCommentsByUserId(userId, pageable.getPageNumber(), pageable.getPageSize(), "asc"))
			        .withSelfRel();
		} catch (Exception e) {
			e.printStackTrace();
		}

        return assembler.toModel(commentsVosPage, link);		
    }
    
    public PagedModel<EntityModel<CommentBasicVO>> findCommentsByPostId(Long postId, Pageable pageable) {
		logger.info("Finding comments for post id!");  
		
		var commentsPage = repository.findCommentsByPostIdPage(postId, pageable);
		
		var commentsVosPage = commentsPage.map(c -> DozerMapper.parseObject(c, CommentBasicVO.class));

		commentsVosPage.map(comment -> {
            try {
                return comment.add(linkTo(methodOn(CommentController.class)
                        .findById(comment.getKey())).withRel("comment-details"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return comment;
        });
		
        Link link = null;
		try {
			link = linkTo(methodOn(CommentController.class)
			        .findCommentsByUserId(postId, pageable.getPageNumber(), pageable.getPageSize(), "asc"))
			        .withSelfRel();
		} catch (Exception e) {
			e.printStackTrace();
		}

        return assembler.toModel(commentsVosPage, link);   	
    }    
	
	public CommentVO create(CommentVO comment) throws Exception {
	    if (comment == null) throw new RequiredObjectIsNullException();
	    
	    var entity = DozerMapper.parseObject(comment, Comment.class);
	    
	    var savedEntity = repository.save(entity);
	    
	    var fullComment = repository.findByIdWithRelations(savedEntity.getId())
	        .orElseThrow(() -> new RuntimeException("Comment not found"));
	    
	    var vo = DozerMapper.parseObject(fullComment, CommentVO.class);	
	    
	    vo.add(linkTo(methodOn(CommentController.class).findById(vo.getKey())).withRel("comment-details"));
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
		vo.add(linkTo(methodOn(CommentController.class).findById(vo.getKey())).withRel("comment-details"));
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one comment!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}

}

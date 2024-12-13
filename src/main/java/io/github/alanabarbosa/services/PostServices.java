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

import io.github.alanabarbosa.controllers.PostController;
import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.model.User;
import io.github.alanabarbosa.repositories.CommentRepository;
import io.github.alanabarbosa.repositories.PostRepository;
import io.github.alanabarbosa.util.NormalizeSlug;

@Service
public class PostServices {
	
	private Logger logger = Logger.getLogger(PostServices.class.getName());
	
	@Autowired
	PostRepository repository;
	
    @Autowired
    private CommentRepository commentRepository;
    
    public List<PostVO> findAll() {
        logger.info("Finding all posts!");

        List<PostVO> posts = DozerMapper.parseListObjects(repository.findAll(), PostVO.class);
        posts.forEach(post -> {
            try {
                List<Comment> comments = commentRepository.findByPostId(post.getKey());
                List<CommentVO> commentVOs = comments.stream()
                    .filter(comment -> comment.getStatus() == true)
                    .map(comment -> {
                        CommentVO commentVO = DozerMapper.parseObject(comment, CommentVO.class);
                       // UserVO userVO = DozerMapper.parseObject(comment.getUser(), UserVO.class);
                        return commentVO;
                    })
                    .collect(Collectors.toList());

                post.setComments(commentVOs);
                post.add(linkTo(methodOn(PostController.class).findById(post.getKey())).withSelfRel());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error while processing comments for post " + post.getKey(), e);
            }
        });
        return posts;
    }

	
    public PostVO findById(Long id) throws Exception {
        logger.info("Finding one post by ID!");
        
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        
        var vo = DozerMapper.parseObject(entity, PostVO.class);
        try {
            List<Comment> comments = commentRepository.findByPostId(id);
            List<CommentVO> commentVOs = DozerMapper.parseListObjects(comments, CommentVO.class);
            vo.setComments(commentVOs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while processing comments for post " + id, e);
        }
        
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
	    var entity = repository.findById(post.getKey())
	            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
	    
	    entity.setTitle(post.getTitle());
	    entity.setContent(post.getContent());
	    
	    var slugFormatted = NormalizeSlug.normalizeString(entity.getTitle());
	    if (!slugFormatted.isEmpty()) entity.setSlug(slugFormatted);
	    
	    entity.setUpdatedAt(post.getUpdatedAt());
	    entity.setStatus(post.getStatus());
	    
	    if (entity.getStatus() == false) entity.setPublishedAt(null);	    
	    var user = DozerMapper.parseObject(post.getUser(), User.class);	    
	    entity.setUser(user);
	    
	    var category = DozerMapper.parseObject(post.getCategory(), Category.class);
	    entity.setCategory(category);
	    
	    entity.setImageDesktop(post.getImageDesktop());
	    entity.setImageMobile(post.getImageMobile());
	    
	    var updatedPost = repository.save(entity);
	    var vo = DozerMapper.parseObject(repository.save(updatedPost), PostVO.class);
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

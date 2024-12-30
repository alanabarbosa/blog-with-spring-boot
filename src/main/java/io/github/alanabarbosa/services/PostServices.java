package io.github.alanabarbosa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
//import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.controllers.CategoryController;
import io.github.alanabarbosa.controllers.CommentController;
import io.github.alanabarbosa.controllers.PostController;
import io.github.alanabarbosa.controllers.UserController;
import io.github.alanabarbosa.data.vo.v1.CommentBasicVO;
import io.github.alanabarbosa.data.vo.v1.PostBasicVO;
import io.github.alanabarbosa.data.vo.v1.PostResponseBasicVO;
import io.github.alanabarbosa.data.vo.v1.PostResponseVO;
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
import io.github.alanabarbosa.util.ConvertToVO;
import io.github.alanabarbosa.util.HateoasUtils;
import io.github.alanabarbosa.util.NormalizeSlug;
import jakarta.transaction.Transactional;

@Service
public class PostServices {
	
	private Logger logger = Logger.getLogger(PostServices.class.getName());
	
	@Autowired
	PostRepository repository;
	
    @Autowired
    CommentRepository commentRepository;
    
	@Autowired
	PagedResourcesAssembler<PostResponseBasicVO> assembler;
	
	@Autowired
	PagedResourcesAssembler<PostBasicVO> assemblerBasic;
    
    public PagedModel<EntityModel<PostResponseBasicVO>> findAll(Pageable pageable) {
        logger.info("Finding all posts!");
        
        var postPage = repository.findAll(pageable);
        
        var postVosPage = postPage.map(p -> DozerMapper.parseObject(p, PostResponseBasicVO.class));
        
        postVosPage.map(post -> {
        	try {
				post.add(linkTo(methodOn(PostController.class)
						.findById(post.getKey())).withRel("post-details"));
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error while processing posts " + post.getKey(), e);
			}
        	return post;
        });
        
        Link link = linkTo(methodOn(PostController.class).findAll(
        		pageable.getPageNumber(),
        		pageable.getPageSize(), 
        		"asc")).withSelfRel();
        
        return assembler.toModel(postVosPage, link);
    }

    public PostResponseVO findById(Long id) throws Exception {
        logger.info("Finding one post by Id!");
        
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this Id"));
        
        var vo = DozerMapper.parseObject(entity, PostResponseVO.class);
                
        List<Comment> comments = commentRepository.findByPostId(id);
        List<CommentBasicVO> commentVOs = ConvertToVO
        		.processEntities(id, comments, CommentBasicVO.class, "Error while processing comments for post");
        vo.setComments(commentVOs);
        
        vo.add(linkTo(methodOn(PostController.class).findById(id)).withRel("post-details"));
        
        vo.getUser().add(linkTo(methodOn(UserController.class)
        		.findById(vo.getUser().getKey())).withRel("user-details"));
        
        vo.getCategory().add(linkTo(methodOn(CategoryController.class)
        		.findById(vo.getUser().getKey())).withRel("category-details"));
        
        HateoasUtils.addCommentLinks(vo.getComments());
        return vo;
    }
	
    /*public List<PostBasicVO> findPostsByCommentId(Long commentId) {
        //var comments = repository.findPostsByCommentId(commentId);
        //return DozerMapper.parseListObjects(comments, PostResponseVO.class);

    	var posts = repository.findPostsByCommentId(commentId);
    	var postsResponseVO = DozerMapper.parseListObjects(posts, PostBasicVO.class);

    	return postsResponseVO.stream()
    	    .map(post -> {
    	        try {
    	            post.add(linkTo(methodOn(CommentController.class).findById(post.getKey())).withRel("post-details"));
    	            return post;
    	        } catch (Exception e) {
    	            logger.severe("Error adding HATEOAS link: " + e.getMessage());
    	            return post;
    	        }
    	    })
    	    .collect(Collectors.toList());    
    }*/
    
   public PagedModel<EntityModel<PostResponseBasicVO>> findPostsByUserId(Long userId, Pageable pageable) {
        logger.info("Finding posts for user id!");  

        var postsPage = repository.findPostsByUserIdPage(userId, pageable);

        var postsVosPage = postsPage.map(p -> DozerMapper.parseObject(p, PostResponseBasicVO.class));

        postsVosPage.map(post -> {
            try {
                return post.add(linkTo(methodOn(PostController.class)
                        .findById(post.getKey())).withRel("post-details"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return post;
        });

        Link link = null;
		try {
			link = linkTo(methodOn(PostController.class)
			        .findPostsByUserId(userId, pageable.getPageNumber(), pageable.getPageSize(), "asc"))
			        .withSelfRel();
		} catch (Exception e) {
			e.printStackTrace();
		}

        return assembler.toModel(postsVosPage, link);
    }
    
    public PagedModel<EntityModel<PostResponseBasicVO>> findByCategoryId(Long userId, Pageable pageable) {     
    	
        logger.info("Finding posts for category id!");  

        var postsPage = repository.findByCategoryIdPage(userId, pageable);

        var postsVosPage = postsPage.map(p -> DozerMapper.parseObject(p, PostResponseBasicVO.class));

        postsVosPage.map(post -> {
            try {
                return post.add(linkTo(methodOn(PostController.class)
                        .findById(post.getKey())).withRel("post-details"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return post;
        });

        Link link = null;
		try {
			link = linkTo(methodOn(PostController.class)
			        .findPostsByUserId(userId, pageable.getPageNumber(), pageable.getPageSize(), "asc"))
			        .withSelfRel();
		} catch (Exception e) {
			e.printStackTrace();
		}

        return assembler.toModel(postsVosPage, link);
    }  
    
	public PostVO create(PostVO post) throws Exception {		
		logger.info("Creating one post!");
		
		if (post == null) throw new RequiredObjectIsNullException();
		if (post != null) post.setCreatedAt(LocalDateTime.now());
		
	    if (post.getImageDesktop() != null && post.getImageDesktop().getId() == null) {
	        post.setImageDesktop(null);
	    }
	    
	    if (post.getImageMobile() != null && post.getImageMobile().getId() == null) {
	        post.setImageMobile(null);
	    }
	    
	    if (post.getCategory() != null && post.getCategory().getKey() == null) {
	        throw new IllegalArgumentException("Category ID cannot be null");
	    }
		
		var entity = DozerMapper.parseObject(post, Post.class);		
		
		if (entity.getStatus()) entity.setPublishedAt(LocalDateTime.now());
		else entity.setPublishedAt(null);
		
	    var slugFormatted = NormalizeSlug.normalizeString(entity.getTitle());	    
	    if (!slugFormatted.isEmpty()) entity.setSlug(slugFormatted);
		
	    var savedPost = repository.save(entity);		
		var vo =  DozerMapper.parseObject(savedPost, PostVO.class);
	    vo.add(linkTo(methodOn(PostController.class).findById(vo.getKey())).withSelfRel());
	    vo.add(linkTo(methodOn(CommentController.class)
	    		.findCommentsByPostId(vo.getKey(), 0, 12, "asc")).withRel("comments-details"));
	    
	    return vo;
	}
	
	public PostVO update(PostVO post) throws Exception {
		logger.info("Updating one post!");
		
	    if (post == null) throw new RequiredObjectIsNullException();
	    
	    if (post != null) post.setCreatedAt(LocalDateTime.now());
	    
	    if (post.getImageDesktop() != null && post.getImageDesktop().getId() == null) {
	        post.setImageDesktop(null);
	    }
	    
	    if (post.getImageMobile() != null && post.getImageMobile().getId() == null) {
	        post.setImageMobile(null);
	    }    
	    
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
	    vo.add(linkTo(methodOn(CommentController.class)
	    		.findCommentsByPostId(vo.getKey(), 0, 12, "asc")).withRel("comments-details"));
	    
	    return vo;
	}
	
	@Transactional
	public PostVO disablePost(Long id) throws Exception {
        logger.info("Disabling one Post!");
        
        repository.disablePost(id);
        
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        
        if (!entity.getStatus()) entity.setPublishedAt(null);
        
        var vo = DozerMapper.parseObject(entity, PostVO.class);
       
        
        vo.add(linkTo(methodOn(PostController.class).findById(id)).withSelfRel());        
        return vo;
    }	
	
	public void delete(Long id) {
		logger.info("Deleting one post!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}
}

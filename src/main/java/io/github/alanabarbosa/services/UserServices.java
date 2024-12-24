package io.github.alanabarbosa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.controllers.CommentController;
import io.github.alanabarbosa.controllers.PostController;
import io.github.alanabarbosa.controllers.UserController;
import io.github.alanabarbosa.data.vo.v1.UserResponseVO;
import io.github.alanabarbosa.data.vo.v1.UserVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Role;
import io.github.alanabarbosa.model.User;
import io.github.alanabarbosa.repositories.CommentRepository;
import io.github.alanabarbosa.repositories.RoleRepository;
import io.github.alanabarbosa.repositories.UserRepository;
import io.github.alanabarbosa.util.PasswordUtil;
import jakarta.transaction.Transactional;

@Service
public class UserServices implements UserDetailsService {
	
	private Logger logger = Logger.getLogger(UserServices.class.getName());
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
    private CommentRepository commentRepository;	
	
	public UserServices(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Finding one user by name " + username + "!");
		var user = repository.findByUsername(username);
		if (user != null) {
			return user;
		} else {
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}
	}
	
	public List<UserResponseVO> findAll() {        
	    logger.info("Finding all users!");        
	    
	    var usersResponseVO = DozerMapper.parseListObjects(repository.findAll(), UserResponseVO.class);

	    return usersResponseVO.stream()
	        .filter(user -> user.getEnabled() != null && user.getEnabled())
	        .map(user -> {
	            try {
	                user.add(linkTo(methodOn(UserController.class).findById(user.getKey())).withSelfRel());
	                user.add(linkTo(methodOn(CommentController.class).findCommentsByUserId(user.getKey())).withRel("comments"));
	                user.add(linkTo(methodOn(PostController.class).findPostsByUserId(user.getKey())).withRel("posts"));
	                return user;
	            } catch (Exception e) {
	                logger.severe("Error adding HATEOAS link: " + e.getMessage());
	                return user;
	            }
	        })
	        .collect(Collectors.toList());
	}


	
	public UserResponseVO findById(Long id) throws Exception {		
		logger.info("Finding one user!");	
		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));	    
	    var vo = DozerMapper.parseObject(entity, UserResponseVO.class);	
	    
	    vo.add(linkTo(methodOn(UserController.class).findById(id)).withSelfRel());
	    vo.add(linkTo(methodOn(CommentController.class).findCommentsByUserId(vo.getKey())).withRel("comments"));
	    vo.add(linkTo(methodOn(PostController.class).findPostsByUserId(vo.getKey())).withRel("posts"));
	    return vo;
	}
	
	@Transactional
	public UserVO create(UserVO user) throws Exception {
		logger.info("Creating one user!");
	    if (user == null) throw new RequiredObjectIsNullException();
	    
	    if (user.getEnabled() == true) user.setCreatedAt(LocalDateTime.now()); 
	    
	    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
	        throw new IllegalArgumentException("Password is required and cannot be empty");
	    }
	    
	    if (user != null) user.setFile(null);
	    
	    var entity = DozerMapper.parseObject(user, User.class);
	    
	    String encodedPassword = PasswordUtil.encodePassword(user.getPassword());
	    entity.setPassword(encodedPassword);	    

	    if (entity.getEnabled()) {
	    	entity.setCreatedAt(LocalDateTime.now());
		    entity.setAccountNonExpired(true);
		    entity.setAccountNonLocked(true);
		    entity.setCredentialsNonExpired(true);	    	
	    }
	    else {
	    	entity.setCreatedAt(null);
		    entity.setAccountNonExpired(false);
		    entity.setAccountNonLocked(false);
		    entity.setCredentialsNonExpired(false);	  	    	
	    }

	    Role defaultRole = roleRepository.findById(2L)
	        .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

	    List<Role> roles = user.getRoles() != null ? user.getRoles().stream()
	        .map(roleVo -> roleRepository.findById(roleVo.getId())
	            .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleVo.getId())))
	        .collect(Collectors.toList()) : new ArrayList<>();

	    if (!roles.contains(defaultRole)) {
	        roles.add(defaultRole);
	    }

	    entity.setRoles(roles);  

	    var savedEntity = repository.save(entity);
	    var vo = DozerMapper.parseObject(savedEntity, UserVO.class);
	    
	    vo.add(linkTo(methodOn(UserController.class).findById(vo.getKey())).withSelfRel());
	    return vo;
	}

	
	public UserVO update(UserVO user) throws Exception {
	    if (user == null) throw new RequiredObjectIsNullException();
	    logger.info("Updating one user!");		

	    var entity =  repository.findById(user.getKey())
	        .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
	    
	    entity.setFirstName(user.getFirstName());
	    entity.setLastName(user.getLastName());
	    entity.setUserName(user.getUserName());
	    entity.setBio(user.getBio());
	    entity.setEnabled(user.getEnabled());
	    
	    if (entity.getEnabled()) entity.setCreatedAt(LocalDateTime.now());
	    else entity.setCreatedAt(null);
	    
	    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
	        entity.setPassword(PasswordUtil.encodePassword(user.getPassword())); 
	    }
	    
	    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
	        List<Role> roles = user.getRoles().stream()
	            .map(roleVo -> roleRepository.findById(roleVo.getId())
	                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleVo.getId())))
	            .collect(Collectors.toList());
	        entity.setRoles(roles);
	    }	    
	    
	    var vo = DozerMapper.parseObject(repository.save(entity), UserVO.class);		
	    vo.add(linkTo(methodOn(UserController.class).findById(vo.getKey())).withSelfRel());
	    vo.add(linkTo(methodOn(CommentController.class).findCommentsByUserId(vo.getKey())).withRel("comments"));
	    return vo;
	}
	
	@Transactional
	public UserVO disableUser(Long id) throws Exception {
        logger.info("Disabling one User!");
        
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        
        //if (!entity.getEnabled()) entity.setPublishedAt(null);
        
        var vo = DozerMapper.parseObject(entity, UserVO.class);
       
        /* try {
            List<Comment> comments = commentRepository.findByPostId(id);
            List<CommentResponseVO> commentVOs = DozerMapper.parseListObjects(comments, CommentResponseVO.class);
            vo.setComments(commentVOs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while processing comments for post " + id, e);
        }*/
        
        vo.add(linkTo(methodOn(UserController.class).findById(id)).withSelfRel());        
        return vo;
    }	

	
	public void delete(Long id) {
		logger.info("Deleting one user!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}	
	
	
}

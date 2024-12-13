package io.github.alanabarbosa.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.alanabarbosa.controllers.UserController;
import io.github.alanabarbosa.data.vo.v1.UserVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Role;
import io.github.alanabarbosa.model.User;
import io.github.alanabarbosa.repositories.RoleRepository;
import io.github.alanabarbosa.repositories.UserRepository;
import io.github.alanabarbosa.util.PasswordUtil;

@Service
public class UserServices implements UserDetailsService {
	
	private Logger logger = Logger.getLogger(UserServices.class.getName());
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	RoleRepository roleRepository;
	
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
	
	public List<UserVO> findAll() {		
	    logger.info("Finding all users!");		
	    var users = DozerMapper.parseListObjects(repository.findAll(), UserVO.class);

	    users.forEach(user -> logger.info("User: " + user.toString()));
	    
	    return users.stream()
	    	.filter(user -> user.getEnabled() == true)
	        .map(user -> {
	            try {
	                user.add(linkTo(methodOn(UserController.class).findById(user.getKey())).withSelfRel());
	                return user;
	            } catch (Exception e) {
	                logger.severe("Error adding HATEOAS link: " + e.getMessage());
	                return user;
	            }
	        })
	        .collect(Collectors.toList());
	}

	
	public UserVO findById(Long id) throws Exception {		
		logger.info("Finding one user!");		
	    var entity = repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));	    
	    var vo = DozerMapper.parseObject(entity, UserVO.class);	    
	    vo.add(linkTo(methodOn(UserController.class).findById(id)).withSelfRel());
	    return vo;
	}
	
	public UserVO create(UserVO user) throws Exception {
	    if (user == null) throw new RequiredObjectIsNullException();
	    
	    logger.info("Attempting to create user: " + user.getUserName());
	    
	    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
	        throw new IllegalArgumentException("Password is required and cannot be empty");
	    }    
	    
	    var entity = DozerMapper.parseObject(user, User.class);
	    
	    String encodedPassword = PasswordUtil.encodePassword(user.getPassword());
	    entity.setPassword(encodedPassword);    
	    
	    entity.setAccountNonExpired(true);
	    entity.setAccountNonLocked(true);
	    entity.setCredentialsNonExpired(true);
	   // entity.setEnabled(true);
	    
	    if (entity.getEnabled()) entity.setCreatedAt(LocalDateTime.now());
	    else entity.setCreatedAt(null);

	    Role defaultRole = roleRepository.findById(2L)
	        .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
	    entity.setRoles(Collections.singletonList(defaultRole));    

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
	            .map(roleVo -> roleRepository.findById(roleVo.getKey())
	                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleVo.getKey())))
	            .collect(Collectors.toList());
	        entity.setRoles(roles);
	    }	    
	    
	    var vo = DozerMapper.parseObject(repository.save(entity), UserVO.class);		
	    vo.add(linkTo(methodOn(UserController.class).findById(vo.getKey())).withSelfRel());
	    return vo;
	}

	
	public void delete(Long id) {
		logger.info("Deleting one user!");
		
		var entity =  repository.findById(id)
	    		.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		
		repository.delete(entity);
	}	
	
	
}

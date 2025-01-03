package io.github.alanabarbosa.unittests.mockito.services;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.model.User;
import io.github.alanabarbosa.repositories.CategoryRepository;
import io.github.alanabarbosa.repositories.CommentRepository;
import io.github.alanabarbosa.repositories.PostRepository;
import io.github.alanabarbosa.repositories.UserRepository;
import io.github.alanabarbosa.services.PostServices;
import io.github.alanabarbosa.unittests.mapper.mocks.MockPost;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PostServicesTest {
	
	MockPost input;
	
	@InjectMocks
	PostServices service;
	
	@Mock
	PostRepository repository;
	
	@Mock
    UserRepository userRepository;
	
	@Mock
    CategoryRepository categoryRepository;
    
	@Mock
	CommentRepository commentRepository;

	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockPost();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() throws Exception {
		Post post = input.mockEntity();
		post.setId(1L);
		
		LocalDateTime now = LocalDateTime.now();
		
	    post.setCreatedAt(now);
	    post.setUpdatedAt(now);
	    post.setPublishedAt(now);
		
		when(repository.findById(1L)).thenReturn(Optional.of(post));
		
		var result = service.findById(1L);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		
		System.out.println("toString findbyid post " + result.toString());
		System.out.println("Result getCategory: " + result.getCategory());
		System.out.println("Result getUser: " + result.getUser());
		
		assertTrue(result.toString().contains("[</api/post/v1/1>;rel=\"post-details\"]"));
		assertEquals("Meu Título0", result.getTitle());
		assertEquals("Este é o conteúdo do post.0", result.getContent());
		assertEquals("meu-titulo0", result.getSlug());
	    assertEquals(now, result.getCreatedAt());
	    assertEquals(now, result.getUpdatedAt());
	    assertEquals(now, result.getPublishedAt());
		assertEquals(true, result.getStatus());
		assertTrue(result.getCategory().toString().contains("[</api/category/v1/1>;rel=\"category-details\"]"));
		assertTrue(result.getUser().toString().contains("[</api/user/v1/1>;rel=\"user-details\"]"));
		assertEquals(new File(), result.getImageDesktop());
		assertEquals(new File(), result.getImageMobile());
	}

	@Test
	void testCreate() throws Exception {
	    Post entity = input.mockEntity(1); 
	    entity.setId(1L);
	    entity.getCategory().setId(1L);

	    Post persisted = entity;
	    persisted.setId(1L);
	    persisted.getCategory().setId(1L);

	    PostVO vo = input.mockVO(1);
	    vo.setKey(1L);	    
	    vo.getCategory().setKey(1L);
	    
	    User mockUser = new User();
	    mockUser.setId(2L);
	    when(userRepository.findById(2L)).thenReturn(Optional.of(mockUser));	
	    
	    Category mockCategory = new Category();
	    mockCategory.setId(1L);
	    when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));	    

	    LocalDateTime now = LocalDateTime.now();

	    vo.setCreatedAt(now);
	    vo.setUpdatedAt(now);
	    vo.setPublishedAt(now);

	    when(repository.save(any(Post.class))).thenReturn(persisted);

	    var result = service.create(vo);

	    assertNotNull(result);
	    assertNotNull(result.getKey());
	    assertNotNull(result.getLinks());
	    
	    System.out.println("toString Create Post " + result.toString());
	    
	    assertTrue(result.toString().contains("[</api/post/v1/1>;rel=\"self\"]"));
	    assertEquals("Meu Título1", result.getTitle());
	    assertEquals("Este é o conteúdo do post.1", result.getContent());
	    assertEquals("meu-titulo1", result.getSlug());
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getPublishedAt().truncatedTo(ChronoUnit.SECONDS));

	    assertEquals(result.getCategory().getKey(), result.getCategory().getKey());
	    assertEquals(new File(), result.getImageDesktop());
	    assertEquals(new File(), result.getImageMobile());
	    assertEquals(2L, result.getUser().getKey());

	    assertEquals(entity.getId(), result.getKey());
	    assertEquals(entity.getTitle(), result.getTitle());
	    assertEquals(entity.getContent(), result.getContent());
	}
	
	@Test
	void testCreateWithNullPost() throws Exception {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});
	    
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(actualMessage));
	}	

	@Test
	void testUpdate() throws Exception {
	    Post existingEntity = input.mockEntity(1);
	    existingEntity.setId(1L);

	    PostVO vo = input.mockVO(1);
	    vo.setKey(1L);

	    LocalDateTime now = LocalDateTime.now();
	    vo.setCreatedAt(now);
	    vo.setUpdatedAt(now);
	    vo.setPublishedAt(now);

	    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);

	    when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));
	    when(repository.save(captor.capture())).thenReturn(existingEntity);

	   var result = service.update(vo);
	    
	    assertNotNull(result);
	    assertNotNull(result.getKey());
	    assertNotNull(result.getLinks());
	    

	    assertTrue(result.toString().contains("[</api/post/v1/1>;rel=\"self\"]"));
	    assertEquals("Meu Título1", result.getTitle());
	    assertEquals("Este é o conteúdo do post.1", result.getContent());
	    assertEquals("meu-titulo1", result.getSlug());
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
	    if (result.getStatus()) assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getPublishedAt().truncatedTo(ChronoUnit.SECONDS));

	    assertEquals(new CategoryVO(), result.getCategory());
	    assertNull(result.getImageDesktop());
	    assertNull(result.getImageMobile());
	    assertEquals(2L, result.getUser().getKey());

	    Post capturedPost = captor.getValue();
	    assertNotNull(capturedPost);
	    assertEquals(existingEntity.getId(), capturedPost.getId());
	}

	
	@Test
	void testUpdateWithNullPost() throws Exception {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
	    
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(actualMessage));
	}	
	
	@Test
	void testDelete() {
		Post entity = input.mockEntity(1); 
		entity.setId(1L);		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));		
		service.delete(1L);
	}

}

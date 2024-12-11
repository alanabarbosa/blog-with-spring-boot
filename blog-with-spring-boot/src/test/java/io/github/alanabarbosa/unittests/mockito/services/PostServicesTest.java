package io.github.alanabarbosa.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
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
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.repositories.CommentRepository;
import io.github.alanabarbosa.repositories.PostRepository;
import io.github.alanabarbosa.services.PostServices;
import io.github.alanabarbosa.unittests.mapper.mocks.MockPost;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PostServicesTest {
	
	MockPost input;
	
	@InjectMocks
	private PostServices service;
	
	@Mock
	PostRepository repository;
	
	@Mock
	private CommentRepository commentRepository;

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
		
		assertTrue(result.toString().contains("[</api/post/v1/1>;rel=\"self\"]"));
		assertEquals("Meu Título0", result.getTitle());
		assertEquals("Este é o conteúdo do post.0", result.getContent());
		assertEquals("meu-titulo0", result.getSlug());
	    assertEquals(now, result.getCreatedAt());
	    assertEquals(now, result.getUpdatedAt());
	    assertEquals(now, result.getPublishedAt());
		assertEquals(true, result.getStatus());
		assertEquals(new CategoryVO(), result.getCategory());
		assertEquals(new File(), result.getImageDesktop()); 
		assertEquals(new File(), result.getImageMobile());
		assertEquals(1L, result.getUser().getId());		
	}

	@Test
	void testFindAll() {
		List<Post> list = input.mockEntityList();
		
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		
		when(repository.findAll()).thenReturn(list);
		when(commentRepository.findByPostId(anyLong())).thenReturn(Collections.emptyList());
		var post = service.findAll();
		
		assertNotNull(post);
		assertEquals(14, post.size());
		
		var postOne = post.get(1);		
		assertNotNull(postOne);
		assertNotNull(postOne.getKey());
		
		assertNotNull(postOne.getLinks());	
		assertTrue(postOne.toString().contains("[</api/post/v1/1>;rel=\"self\"]"));
		
		assertEquals("Meu Título1", postOne.getTitle());
		assertEquals("Este é o conteúdo do post.1", postOne.getContent());
		assertEquals("meu-titulo1", postOne.getSlug());
	    assertEquals(now, postOne.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now, postOne.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now, postOne.getPublishedAt().truncatedTo(ChronoUnit.SECONDS));
		assertEquals(false, postOne.getStatus());
		assertEquals(new CategoryVO(), postOne.getCategory());
		assertEquals(new File(), postOne.getImageDesktop()); 
		assertEquals(new File(), postOne.getImageMobile());
		assertEquals(2L, postOne.getUser().getId());
		
		var postFour = post.get(4);		
		assertNotNull(postFour);
		assertNotNull(postFour.getKey());
		assertNotNull(postFour.getLinks());		
		assertTrue(postFour.toString().contains("[</api/post/v1/4>;rel=\"self\"]"));
		assertEquals("Meu Título4", postFour.getTitle());
		assertEquals("Este é o conteúdo do post.4", postFour.getContent());
		assertEquals("meu-titulo4", postFour.getSlug());
	    assertEquals(now, postOne.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now, postOne.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now, postOne.getPublishedAt().truncatedTo(ChronoUnit.SECONDS));
		assertEquals(true, postFour.getStatus());
		assertEquals(new CategoryVO(), postFour.getCategory());
		assertEquals(new File(), postFour.getImageDesktop()); 
		assertEquals(new File(), postFour.getImageMobile());
		assertEquals(5L, postFour.getUser().getId());
		
		var postSeven = post.get(7);		
		assertNotNull(postSeven);
		assertNotNull(postSeven.getKey());
		assertNotNull(postSeven.getLinks());		
		assertTrue(postSeven.toString().contains("[</api/post/v1/7>;rel=\"self\"]"));
		assertEquals("Meu Título7", postSeven.getTitle());
		assertEquals("Este é o conteúdo do post.7", postSeven.getContent());
		assertEquals("meu-titulo7", postSeven.getSlug());
	    assertEquals(now, postOne.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now, postOne.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now, postOne.getPublishedAt().truncatedTo(ChronoUnit.SECONDS));
		assertEquals(false, postSeven.getStatus());
		assertEquals(new CategoryVO(), postSeven.getCategory());
		assertEquals(new File(), postSeven.getImageDesktop()); 
		assertEquals(new File(), postSeven.getImageMobile());
		assertEquals(8L, postSeven.getUser().getId());		
	}

	@Test
	void testCreate() throws Exception {
	    Post entity = input.mockEntity(1); 
	    entity.setId(1L);

	    Post persisted = entity;
	    persisted.setId(1L);

	    PostVO vo = input.mockVO(1);
	    vo.setKey(1L);

	    LocalDateTime now = LocalDateTime.now();

	    vo.setCreatedAt(now);
	    vo.setUpdatedAt(now);
	    vo.setPublishedAt(now);

	    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
	    
	    when(repository.save(captor.capture())).thenReturn(persisted);

	    var result = service.create(vo);

	    assertNotNull(result);
	    assertNotNull(result.getKey());
	    assertNotNull(result.getLinks());
	    
	    assertTrue(result.toString().contains("[</api/post/v1/1>;rel=\"self\"]"));
	    assertEquals("Meu Título1", result.getTitle());
	    assertEquals("Este é o conteúdo do post.1", result.getContent());
	    assertEquals("meu-titulo1", result.getSlug());
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getPublishedAt().truncatedTo(ChronoUnit.SECONDS));

	    assertEquals(new CategoryVO(), result.getCategory());
	    assertEquals(new File(), result.getImageDesktop()); 
	    assertEquals(new File(), result.getImageMobile());
	    assertEquals(2L, result.getUser().getId());

	    Post capturedPost = captor.getValue();

	    assertEquals(entity.getId(), capturedPost.getId());
	    assertEquals(entity.getTitle(), capturedPost.getTitle());
	    assertEquals(entity.getContent(), capturedPost.getContent());
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
	    assertEquals(new File(), result.getImageDesktop());
	    assertEquals(new File(), result.getImageMobile());
	    assertEquals(2L, result.getUser().getId());

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

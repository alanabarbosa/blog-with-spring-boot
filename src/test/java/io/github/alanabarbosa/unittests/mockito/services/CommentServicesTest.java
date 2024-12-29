package io.github.alanabarbosa.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.repositories.CommentRepository;
import io.github.alanabarbosa.services.CommentServices;
import io.github.alanabarbosa.unittests.mapper.mocks.MockComment;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CommentServicesTest {
	
	MockComment input;
	
	@InjectMocks
	private CommentServices service;
	
	@Mock
	CommentRepository repository;

	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockComment();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() throws Exception {
		Comment comment = input.mockEntity();
		comment.setId(1L);
		
		LocalDateTime now = LocalDateTime.now();
		
	    comment.setCreatedAt(now);
		
		when(repository.findById(1L)).thenReturn(Optional.of(comment));
		var result = service.findById(1L);		
		assertNotNull(result);
		assertNotNull(result.getKey());			
		assertTrue(result.toString().contains("[</api/comment/v1/1>;rel=\"comment-details\"]"));
		assertEquals("Este é um comentario.0", result.getContent());
		assertEquals(now, result.getCreatedAt());
		assertEquals(true, result.getStatus());	
	}

	@Test
	void testFindAll() {
		List<Comment> list = input.mockEntityList();
		
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		
		System.out.println("Tamanho da lista mockada: " + list.size());
		when(repository.findAllWithUser()).thenReturn(list); 
		
		var comment = service.findAll();
		
		System.out.println("tamanho total: " + comment.size());
		System.out.println("tamanho total: " + comment);
		
		assertNotNull(comment);
		assertEquals(14, comment.size());
		
		var commentOne = comment.get(1);		
		assertNotNull(commentOne);
		assertNotNull(commentOne.getKey());		
		assertTrue(commentOne.toString().contains("[</api/comment/v1/1>;rel=\"comment-details\"]"));
		assertEquals("Este é um comentario.1", commentOne.getContent());
		
		var CommentFour = comment.get(4);		
		assertNotNull(CommentFour);
		assertNotNull(CommentFour.getKey());		
		assertTrue(CommentFour.toString().contains("[</api/comment/v1/4>;rel=\"comment-details\"]"));
		assertEquals("Este é um comentario.4", CommentFour.getContent());
		
		var CommentSeven = comment.get(7);		
		assertNotNull(CommentSeven);
		assertNotNull(CommentSeven.getKey());		
		assertTrue(CommentSeven.toString().contains("[</api/comment/v1/7>;rel=\"comment-details\"]"));
		assertEquals("Este é um comentario.7", CommentSeven.getContent());
	
	}

	@Test
	void testCreate() throws Exception {
	    Comment entity = input.mockEntity(1); 
	    entity.setId(1L);

	    Comment persisted = entity;
	    persisted.setId(1L);

	    CommentVO vo = input.mockVO(1);
	    vo.setKey(1L);

	    LocalDateTime now = LocalDateTime.now();

	    vo.setCreatedAt(now);

	    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
	    
	    when(repository.save(captor.capture())).thenReturn(persisted);
	    when(repository.findByIdWithRelations(1L)).thenReturn(Optional.of(entity));

	    var result = service.create(vo);

	    assertNotNull(result);
	    assertNotNull(result.getKey());
	    assertNotNull(result.getLinks());
	    
	    System.out.println("to string: " + result.toString());
	    
	    assertTrue(result.toString().contains("[</api/comment/v1/1>;rel=\"comment-details\"]"));
	    
	    assertEquals("Este é um comentario.1", result.getContent());
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(false, result.getStatus());
	    assertEquals(2L, result.getPost().getKey());
		assertEquals(2L, result.getUser().getKey()); 

	    Comment capturedPost = captor.getValue();

	    assertEquals(entity.getId(), capturedPost.getId());
	    assertEquals(entity.getContent(), capturedPost.getContent());
	}
	
	@Test
	void testCreateWithNullComment() throws Exception {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});
	    
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(actualMessage));
	}	

	@Test
	void testUpdate() throws Exception {
	    Comment existingEntity = input.mockEntity(1);
	    existingEntity.setId(1L);

	    CommentVO vo = input.mockVO(1);
	    vo.setKey(1L);

	    LocalDateTime now = LocalDateTime.now();
	    vo.setCreatedAt(now);

	    ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

	    when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));
	    when(repository.save(captor.capture())).thenReturn(existingEntity);

	    var result = service.update(vo);
	    
	    assertNotNull(result);
	    assertNotNull(result.getKey());
	    
	    System.out.println("tostring update" + result.toString());
	    
	    assertTrue(result.toString().contains("[</api/comment/v1/1>;rel=\"comment-details\"]"));
	    assertEquals("Este é um comentario.1", result.getContent());
	    assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	    assertEquals(2L, result.getPost().getKey());
		assertEquals(2L, result.getUser().getKey());
	    assertEquals(false, result.getStatus());

	    Comment capturedPost = captor.getValue();
	    assertEquals(existingEntity.getId(), capturedPost.getId());
	    assertEquals(existingEntity.getContent(), capturedPost.getContent());
	}
	
	@Test
	void testUpdateWithNullComment() throws Exception {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
	    
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(actualMessage));
	}	
	
	@Test
	void testDelete() {
		Comment entity = input.mockEntity(1); 
		entity.setId(1L);		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));		
		service.delete(1L);
	}
}

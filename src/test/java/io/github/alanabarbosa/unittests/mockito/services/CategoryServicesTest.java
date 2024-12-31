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

import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.exceptions.RequiredObjectIsNullException;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.repositories.CategoryRepository;
import io.github.alanabarbosa.services.CategoryServices;
import io.github.alanabarbosa.unittests.mapper.mocks.MockCategory;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CategoryServicesTest {
	
	MockCategory input;
	
	@InjectMocks
	private CategoryServices service;
	
	@Mock
	CategoryRepository repository;

	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockCategory();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() throws Exception {
		Category category = input.mockEntity();
		category.setId(1L);
		
		LocalDateTime now = LocalDateTime.now();
		
	    category.setCreatedAt(now);
		
		when(repository.findById(1L)).thenReturn(Optional.of(category));
		var result = service.findById(1L);		
		assertNotNull(result);
		assertNotNull(result.getKey());		
		
		System.out.println("toString: " + result.toString());
		
		assertTrue(result.toString().contains("[</api/category/v1/1>;rel=\"category-details\"]"));
		assertEquals("Este é um name.0", result.getName());
	}

	@Test
	void testFindAll() {
		List<Category> list = input.mockEntityList();
		
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		
		when(repository.findAll()).thenReturn(list);
		
		var category = service.findAll();
		
		assertNotNull(category);
		assertEquals(14, category.size());
		
		var categoryOne = category.get(1);		
	    assertNotNull(categoryOne);
		assertNotNull(categoryOne.getKey());	
		
		assertTrue(categoryOne.toString().contains("[</api/category/v1/1>;rel=\"category-details\"]"));
		assertEquals("Este é um name.1", categoryOne.getName());
		
		var categoryFour = category.get(4);		
	    assertNotNull(categoryFour);
		assertNotNull(categoryFour.getKey());			
		assertTrue(categoryFour.toString().contains("[</api/category/v1/4>;rel=\"category-details\"]"));
		assertEquals("Este é um name.4", categoryFour.getName());
		
		var categorySeven = category.get(7);		
	    assertNotNull(categorySeven);
		assertNotNull(categorySeven.getKey());			
		assertTrue(categorySeven.toString().contains("[</api/category/v1/7>;rel=\"category-details\"]"));
		assertEquals("Este é um name.7", categorySeven.getName());
	}

	@Test
	void testCreate() throws Exception {
	    Category entity = input.mockEntity(1); 
	    entity.setId(1L);

	    Category persisted = entity;
	    persisted.setId(1L);

	    CategoryVO vo = input.mockVO(1);
	    vo.setKey(1L);

	    LocalDateTime now = LocalDateTime.now();

	    vo.setCreatedAt(now);

	    ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
	    
	    when(repository.save(captor.capture())).thenReturn(persisted);

	    var result = service.create(vo);

	    assertNotNull(result);
		assertNotNull(result.getKey());			
		assertTrue(result.toString().contains("[</api/category/v1/1>;rel=\"category-details\"]"));
		assertEquals("Este é um name.1", result.getName());
		assertEquals("Este é uma description.1", result.getDescription());
		assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
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
	    Category existingEntity = input.mockEntity(1);
	    existingEntity.setId(1L);

	    CategoryVO vo = input.mockVO(1);
	    vo.setKey(1L);

	    LocalDateTime now = LocalDateTime.now();
	    vo.setCreatedAt(now);

	    ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

	    when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));
	    when(repository.save(captor.capture())).thenReturn(existingEntity);

	   var result = service.update(vo);
	    
	   assertNotNull(result);
		assertNotNull(result.getKey());			
		assertTrue(result.toString().contains("[</api/category/v1/1>;rel=\"category-details\"]"));
		assertEquals("Este é um name.1", result.getName());
		assertEquals("Este é uma description.1", result.getDescription());
		assertEquals(now.truncatedTo(ChronoUnit.SECONDS), result.getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
	}

	
	@Test
	void testUpdateWithNullCategory() throws Exception {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
	    
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(actualMessage));
	}	
	
	@Test
	void testDelete() {
		Category entity = input.mockEntity(1); 
		entity.setId(1L);		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));		
		service.delete(1L);
	}

}

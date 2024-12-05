package io.github.alanabarbosa.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.services.CategoryServices;

@RestController
@RequestMapping("/api/category/v1")
public class CategoryController {
	
	@Autowired
	private CategoryServices service;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CategoryVO> findAll() {
		return service.findAll();
	}
	
	@GetMapping(value="/{id}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public CategoryVO findById(@PathVariable(value = "id") Long id) throws Exception {
		return service.findById(id);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public CategoryVO create(@RequestBody CategoryVO post) throws Exception {
		return service.create(post);
	}
	
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public CategoryVO update(@RequestBody CategoryVO post) throws Exception {
		return service.update(post);
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}	
		
}

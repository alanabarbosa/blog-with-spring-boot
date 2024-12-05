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

import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.services.CommentServices;

@RestController
@RequestMapping("/api/comment/v1")
public class CommentController {
	
	@Autowired
	private CommentServices service;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CommentVO> findAll() {
		return service.findAll();
	}
	
	@GetMapping(value="/{id}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public CommentVO findById(@PathVariable(value = "id") Long id) throws Exception {
		return service.findById(id);
	}
	
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public CommentVO create(@RequestBody CommentVO post) throws Exception {
		return service.create(post);
	}
	
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public CommentVO update(@RequestBody CommentVO post) throws Exception {
		return service.update(post);
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}	
		
}

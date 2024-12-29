package io.github.alanabarbosa.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alanabarbosa.data.vo.v1.CategoryBasicVO;
import io.github.alanabarbosa.data.vo.v1.CategoryResponseVO;
import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.services.CategoryServices;
import io.github.alanabarbosa.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/category/v1")
@Tag(name = "Category", description = "Endpoints for Managing Category")
public class CategoryController {
	
	@Autowired
	private CategoryServices service;
	
	@GetMapping(produces = { MediaType.APPLICATION_JSON, 
			MediaType.APPLICATION_XML, 
			MediaType.APPLICATION_YML })
	@Operation(summary = " Finds all Category", description = "Finds all Category",
		tags = {"Category"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = CategoryBasicVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
	public List<CategoryBasicVO> findAll() {
		return service.findAll();
	}
	
	@GetMapping(value="/{id}",
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = " Finds Category By ID", description = "Finds Category By ID",
		tags = {"Category"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = CategoryResponseVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
	public CategoryResponseVO findById(@PathVariable Long id) throws Exception {
		return service.findById(id);
	}
	
	@PostMapping(			
			consumes = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  },
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = "Adds a new Category", description = "Adds a new  Category",
		tags = {"Category"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = CategoryVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
	public CategoryVO create(@RequestBody CategoryVO category) throws Exception {
		return service.create(category);
	}
	
	@PutMapping(			
			consumes = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  },
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = "Updates a Category", description = "Updates a Category",
		tags = {"Category"},
			responses = {
				@ApiResponse(description = "Success", responseCode = "200,", 
						content = @Content (schema = @Schema(implementation = CategoryVO.class))
				),
				@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)
	public CategoryVO update(@RequestBody CategoryVO category) throws Exception {
		return service.update(category);
	}
	
	@DeleteMapping(value="/{id}")
	@Operation(summary = "Deletes a Category", description = "Deletes a Category",
		tags = {"Category"},
			responses = {
				@ApiResponse(description = "Not content", responseCode = "204,", content = @Content),
				@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
				@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	) 	
	public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}	
		
}

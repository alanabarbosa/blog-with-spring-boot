package io.github.alanabarbosa.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.alanabarbosa.data.vo.v1.CommentBasicVO;
import io.github.alanabarbosa.data.vo.v1.CommentResponseVO;
import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.services.CommentServices;
import io.github.alanabarbosa.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/comment/v1")
@Tag(name = "Comment", description = "Endpoints for Managing Comment")
public class CommentController {
	
	@Autowired
	private CommentServices service;
	
	@GetMapping(produces = { MediaType.APPLICATION_JSON, 
			MediaType.APPLICATION_XML, 
			MediaType.APPLICATION_YML })
	@Operation(summary = " Finds all Comment", description = "Finds all Comment",
		tags = {"Comment"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = CommentBasicVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
    public ResponseEntity<PagedModel<EntityModel<CommentBasicVO>>> findAll(
    		@RequestParam(value = "page", defaultValue = "0") Integer page,
    		@RequestParam(value = "size", defaultValue = "12") Integer size,
    		@RequestParam(value = "direction", defaultValue = "asc") String direction
    		) {
    	
    	var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
    	
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "content"));
		return ResponseEntity.ok(service.findAll(pageable));
    }
	
	@GetMapping(value="/{id}",
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = " Finds Comment By ID", description = "Finds Comment By ID",
		tags = {"Comment"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = CommentResponseVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	) 	
	public CommentResponseVO findById(@PathVariable Long id) throws Exception {
		return service.findById(id);
	}
	
	@GetMapping(value="/user/{userId}",
	    produces = { MediaType.APPLICATION_JSON, 
	                 MediaType.APPLICATION_XML, 
	                 MediaType.APPLICATION_YML })
	@Operation(summary = "Finds Comments By User ID", description = "Finds Comments By User ID",
	    tags = {"Comment"},
	    responses = {
	        @ApiResponse(description = "Success", responseCode = "200,", 
	            content = {
	                @Content(
	                    mediaType = "application/json",
	                    array = @ArraySchema(schema = @Schema(implementation = CommentBasicVO.class))
	                )
	            }),
	        @ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
	        @ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
	        @ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
	        @ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
	    }
	)
	public ResponseEntity<PagedModel<EntityModel<CommentBasicVO>>> findCommentsByUserId(
			@PathVariable Long userId,
	        @RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "12") Integer size,
	        @RequestParam(value = "direction", defaultValue = "asc") String direction) throws Exception {
	
	    var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
	
	    Pageable pageable = PageRequest.of(page, size);
	
	    var postPage = service.findCommentsByUserId(userId, pageable);
	
	    return ResponseEntity.ok(postPage);
	}
	
	@GetMapping(value="/post/{postId}",
		    produces = { MediaType.APPLICATION_JSON, 
		                 MediaType.APPLICATION_XML, 
		                 MediaType.APPLICATION_YML })
		@Operation(summary = "Finds Comments By Post ID", description = "Finds Comments By Post ID",
		    tags = {"Comment"},
		    responses = {
		        @ApiResponse(description = "Success", responseCode = "200,", 
		            content = {
		                @Content(
		                    mediaType = "application/json",
		                    array = @ArraySchema(schema = @Schema(implementation = CommentBasicVO.class))
		                )
		            }),
		        @ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
		        @ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
		        @ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
		        @ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		    }
		)
	public ResponseEntity<PagedModel<EntityModel<CommentBasicVO>>> findCommentsByPostId(
			@PathVariable Long postId,
	        @RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "12") Integer size,
	        @RequestParam(value = "direction", defaultValue = "asc") String direction) throws Exception {
	
	    var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
	
	    Pageable pageable = PageRequest.of(page, size);
	
	    var postPage = service.findCommentsByPostId(postId, pageable);
	
	    return ResponseEntity.ok(postPage);
	}	
	
	@PostMapping(			
			consumes = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  },
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = "Adds a new Comment", description = "Adds a new  Comment",
		tags = {"Comment"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = CommentVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
	public CommentVO create(@RequestBody CommentVO post) throws Exception {
		return service.create(post);
	}
	
	@PutMapping(			
			consumes = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  },
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = "Updates a Comment", description = "Updates a Comment",
		tags = {"Comment"},
			responses = {
				@ApiResponse(description = "Success", responseCode = "200,", 
						content = @Content (schema = @Schema(implementation = CommentVO.class))
				),
				@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
	public CommentVO update(@RequestBody CommentVO post) throws Exception {
		return service.update(post);
	}
	
	@DeleteMapping(value="/{id}")
	@Operation(summary = "Deletes a Comment", description = "Deletes a Comment",
		tags = {"Comment"},
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

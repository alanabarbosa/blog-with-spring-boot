package io.github.alanabarbosa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.alanabarbosa.data.vo.v1.PostResponseBasicVO;
import io.github.alanabarbosa.data.vo.v1.PostResponseVO;
import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.services.PostServices;
import io.github.alanabarbosa.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/post/v1" )
@Tag(name = "Post", description = "Endpoints for Managing Post")
public class PostController {
    
    @Autowired
    private PostServices service;
    
    @GetMapping(produces = { MediaType.APPLICATION_JSON })
    @Operation(summary = "Finds all Post", description = "Finds all Post",
        tags = {"Post"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200,", 
                content = {
                    @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = PostResponseBasicVO.class))
                    )
                }),
            @ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
        }
    )
    public ResponseEntity<PagedModel<EntityModel<PostResponseBasicVO>>> findAll(
    		@RequestParam(value = "page", defaultValue = "0") Integer page,
    		@RequestParam(value = "size", defaultValue = "12") Integer size,
    		@RequestParam(value = "direction", defaultValue = "asc") String direction
    		) {
    	
    	var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
    	
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));
		return ResponseEntity.ok(service.findAll(pageable));
    }
    
    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value="/{id}",
            produces = { MediaType.APPLICATION_JSON })
	@Operation(summary = " Finds Post By ID", description = "Finds Post By ID",
		tags = {"Post"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = PostResponseVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)    
    public PostResponseVO findById(@PathVariable Long id) throws Exception {
        return service.findById(id);
    }
    
   @GetMapping(value = "/user/{userId}",
            produces = { MediaType.APPLICATION_JSON })
	@Operation(summary = "Finds Posts By User ID", description = "Finds Posts By User ID",
	            tags = {"Post"},
	            responses = {
	                @ApiResponse(description = "Success", responseCode = "200,", 
	                             content = {
	                                 @Content(
	                                     mediaType = "application/json",
	                                     array = @ArraySchema(schema = @Schema(implementation = PostResponseBasicVO.class))
	                                 )
	                             }),
	                @ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
	                @ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
	                @ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
	                @ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
	            }
	)
	public ResponseEntity<PagedModel<EntityModel<PostResponseBasicVO>>> findPostsByUserId(
	        @PathVariable Long userId,
	        @RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "12") Integer size,
	        @RequestParam(value = "direction", defaultValue = "asc") String direction) throws Exception {
	
	    var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
	
	    Pageable pageable = PageRequest.of(page, size);
	
	    var postPage = service.findPostsByUserId(userId, pageable);
	
	    return ResponseEntity.ok(postPage);
	}

    @GetMapping(value="/category/{categoryId}",
    	    produces = { MediaType.APPLICATION_JSON, 
    	                 MediaType.APPLICATION_XML, 
    	                 MediaType.APPLICATION_YML })
    	@Operation(summary = "Finds Posts By Category ID", description = "Finds Posts By Category ID",
    	    tags = {"Post"},
    	    responses = {
    	        @ApiResponse(description = "Success", responseCode = "200,", 
    	            content = {
    	                @Content(
    	                    mediaType = "application/json",
    	                    array = @ArraySchema(schema = @Schema(implementation = PostResponseBasicVO.class))
    	                )
    	            }),
    	        @ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
    	        @ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
    	        @ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
    	        @ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
    	    }
    	)
	public ResponseEntity<PagedModel<EntityModel<PostResponseBasicVO>>> findByCategoryId(
	        @PathVariable Long categoryId,
	        @RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "12") Integer size,
	        @RequestParam(value = "direction", defaultValue = "asc") String direction) throws Exception {
	
	    var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
	
	    Pageable pageable = PageRequest.of(page, size);
	
	    var postPage = service.findByCategoryId(categoryId, pageable);
	
	    return ResponseEntity.ok(postPage);
	}  
    
    @CrossOrigin(origins = { "http://localhost:8080", "https://alanabarbosa.com.br"})
    @PostMapping(
            consumes = { MediaType.APPLICATION_JSON },
            produces = { MediaType.APPLICATION_JSON  })
	@Operation(summary = "Adds a new Post", description = "Adds a new  Post",
		tags = {"Post"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = PostVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)    
    public PostVO create(@RequestBody PostVO post) throws Exception {
        return service.create(post);
    }
    
    @PutMapping(
            consumes = { MediaType.APPLICATION_JSON },
            produces = { MediaType.APPLICATION_JSON })
	@Operation(summary = "Updates a Post", description = "Updates a Post",
		tags = {"Post"},
			responses = {
				@ApiResponse(description = "Success", responseCode = "200,", 
						content = @Content (schema = @Schema(implementation = PostVO.class))
				),
				@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)
    public PostVO update(@RequestBody PostVO post) throws Exception {
        return service.update(post);
    }
    
    //@CrossOrigin(origins = "http://localhost:8080")
    @PatchMapping(value="/{id}",
            produces = { MediaType.APPLICATION_JSON })
	@Operation(summary = " Disable a specific Post By ID", description = "Disable a specific Post By ID",
		tags = {"Post"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = PostVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)    
    public PostVO disablePost(@PathVariable Long id) throws Exception {
        return service.disablePost(id);
    } 
    
    @DeleteMapping(value="/{id}")
	@Operation(summary = "Deletes a Post", description = "Deletes a Post",
		tags = {"Post"},
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

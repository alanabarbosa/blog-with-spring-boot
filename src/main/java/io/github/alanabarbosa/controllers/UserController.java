package io.github.alanabarbosa.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alanabarbosa.data.vo.v1.UserResponseBasicVO;
import io.github.alanabarbosa.data.vo.v1.UserResponseVO;
import io.github.alanabarbosa.data.vo.v1.UserVO;
import io.github.alanabarbosa.services.UserServices;
import io.github.alanabarbosa.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user/v1")
@Tag(name = "User", description = "Endpoints for Managing User")
public class UserController {
	
	@Autowired
	private UserServices service;
	
	@GetMapping(produces = { MediaType.APPLICATION_JSON,
			MediaType.APPLICATION_XML,
			MediaType.APPLICATION_YML })
	@Operation(summary = " Finds all User", description = "Finds all User",
		tags = {"User"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = UserVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
	public List<UserResponseBasicVO> findAll() {
		return service.findAll();
	}
	
	@GetMapping(value="/{id}",
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = " Finds User By ID", description = "Finds User By ID",
		tags = {"User"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = UserVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	) 	
	public UserResponseVO findById(@PathVariable Long id) throws Exception {
		return service.findById(id);
	}
	
	@PostMapping(			
			consumes = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  },
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = "Adds a new User", description = "Adds a new  User",
		tags = {"User"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = UserVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
	public UserVO create(@RequestBody UserVO user) throws Exception {	    
	    return service.create(user);
	}
	
	@PutMapping(			
			consumes = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  },
			produces = { MediaType.APPLICATION_JSON, 
					MediaType.APPLICATION_XML, 
					MediaType.APPLICATION_YML  })
	@Operation(summary = "Updates a User", description = "Updates a User",
		tags = {"User"},
			responses = {
				@ApiResponse(description = "Success", responseCode = "200,", 
						content = @Content (schema = @Schema(implementation = UserVO.class))
				),
				@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
				@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
				@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)	
	public UserVO update(@RequestBody UserVO user) throws Exception {
		return service.update(user);
	}
	
	@PatchMapping(value="/{id}",
            produces = { MediaType.APPLICATION_JSON, 
                    MediaType.APPLICATION_XML, 
                    MediaType.APPLICATION_YML  })
	@Operation(summary = " Disable a specific User By ID", description = "Disable a specific User By ID",
		tags = {"User"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200,", 
					content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = UserVO.class))
							)
					}),
			@ApiResponse(description = "Bad Request", responseCode = "400,", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401,", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404,", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500,", content = @Content)
		}
	)    
    public UserVO disableUser(@PathVariable Long id) throws Exception {
        return service.disableUser(id);
    }	
	
	@DeleteMapping(value="/{id}")
	@Operation(summary = "Deletes a User", description = "Deletes a User",
		tags = {"User"},
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

package io.github.alanabarbosa.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.alanabarbosa.data.vo.v1.CategoryResponseBasicVO;
import io.github.alanabarbosa.data.vo.v1.UploadFileResponseVO;
import io.github.alanabarbosa.exceptions.MyFileNotFoundException;
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.repositories.FileRepository;
import io.github.alanabarbosa.services.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file/v1")
@Tag(name = "File", description = "Endpoints for Managing File")
public class FileController {
	
	private Logger logger = Logger.getLogger(FileController.class.getName());
	
	@Autowired
	FileRepository repository;
	
	@Autowired
	private FileStorageService service;
	
	@PostMapping("/uploadFile")
	@Operation(summary = "Uploads a single file", description = "Uploads a single file to the server",
		tags = {"File"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = UploadFileResponseVO.class))),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
		}
	)
	public UploadFileResponseVO uploadFile(@RequestParam("file") MultipartFile file) {
		logger.info("Storing file to disk");
		
		var filename = service.storeFile(file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/file/v1/downloadFile/")
				.path(filename)
				.toUriString();
		
		return new UploadFileResponseVO(filename, fileDownloadUri, file.getContentType(), file.getSize());
	}
	
	@PostMapping("/uploadMultipleFiles")
	@Operation(summary = "Uploads multiple files", description = "Uploads a multiple files to the server",
		tags = {"File"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = UploadFileResponseVO.class))),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
		}
	)	
	public List<UploadFileResponseVO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		logger.info("Storing files to disk");
		
		return Arrays.asList(files).stream()
				.map(file -> uploadFile(file))
				.collect(Collectors.toList());
	}
	
    @GetMapping("/downloadFile/{filename}")
	@Operation(summary = "Download a file", description = "Downloads a file from the server",
		tags = {"File"},
		responses = {
			@ApiResponse(description = "Success", responseCode = "200", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = UploadFileResponseVO.class))),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
		}
	)    
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String filename, HttpServletRequest request) {
        logger.info("Reading a file from database");

        Resource resource = service.loadFileAsResource(filename);
        File fileEntity = repository.findByFilename(filename)
                .orElseThrow(() -> new MyFileNotFoundException("File not found " + filename));

        String contentType = fileEntity.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        logger.info("Download de arquivo '" + filename + "' com tipo de conteúdo: " + contentType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

}

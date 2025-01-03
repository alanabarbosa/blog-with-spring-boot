package io.github.alanabarbosa.services;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.github.alanabarbosa.config.FileStorageConfig;
import io.github.alanabarbosa.exceptions.FileStorageException;
import io.github.alanabarbosa.exceptions.MyFileNotFoundException;
import io.github.alanabarbosa.exceptions.ResourceNotFoundException;
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.model.User;
import io.github.alanabarbosa.repositories.FileRepository;
import io.github.alanabarbosa.repositories.UserRepository;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;
	
	@Autowired
	FileRepository repository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	public FileStorageService(FileStorageConfig fileStorageConfig) {
		Path path = Paths.get(fileStorageConfig.getUploadDir())
				.toAbsolutePath().normalize();
		
		this.fileStorageLocation = path;
		
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception e) {
			throw new FileStorageException("Could not create directory where the uploaded files will be stored!", e);
		}
	}
	
    public String storeFile(MultipartFile file, Long userId) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (filename.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
            }
            
            User user = userRepository.findById(userId)
            		.orElseThrow(() -> new ResourceNotFoundException("User not found"));;

            File fileEntity = new File();
            fileEntity.setFilename(filename);
            fileEntity.setContentType(file.getContentType());
            fileEntity.setData(file.getBytes());
            fileEntity.setUser(user);
            
            if (fileEntity != null) {
            	fileEntity.setCreatedAt(LocalDateTime.now());
            }

            repository.save(fileEntity);

            return filename;
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + filename + ". Please try again!", e);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            File fileEntity = repository.findByFilename(filename)
                    .orElseThrow(() -> new MyFileNotFoundException("File not found " + filename));


            return new InputStreamResource(new ByteArrayInputStream(fileEntity.getData()));
        } catch (Exception e) {
            throw new MyFileNotFoundException("File not found " + filename, e);
        }
    }
}

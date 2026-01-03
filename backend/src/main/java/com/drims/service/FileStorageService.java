package com.drims.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.upload.dir}")
    private String uploadDir;
    
    public String storeFile(MultipartFile file, String facultyId, String category) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        // Validate PDF
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }
        
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, facultyId, category);
            Files.createDirectories(uploadPath);
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : ".pdf";
            String filename = UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return relative path
            return Paths.get(facultyId, category, filename).toString().replace("\\", "/");
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }
    
    public Path loadFile(String filePath) {
        return Paths.get(uploadDir).resolve(filePath);
    }
}


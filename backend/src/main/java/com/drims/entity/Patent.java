package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "patents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patent {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile
    
    private String title;
    private String patentNumber;
    private String inventors;
    private Integer year;
    private String country;
    private String status; // Granted, Filed, Pending
    
    private String proofDocumentPath; // PDF file path
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


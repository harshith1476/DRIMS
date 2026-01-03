package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "journals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Journal {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile
    
    private String title;
    private String journalName;
    private String authors;
    private Integer year;
    private String volume;
    private String issue;
    private String pages;
    private String doi;
    private String impactFactor;
    private String status; // Published, Accepted, Submitted
    
    private String proofDocumentPath; // PDF file path
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


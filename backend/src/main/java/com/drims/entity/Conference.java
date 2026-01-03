package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "conferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conference {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile
    
    private String title;
    private String conferenceName;
    private String authors;
    private Integer year;
    private String location;
    private String date;
    private String status; // Published, Accepted, Submitted
    
    private String proofDocumentPath; // PDF file path
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


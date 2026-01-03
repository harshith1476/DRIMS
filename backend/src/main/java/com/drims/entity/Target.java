package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "targets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Target {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile
    
    private Integer year;
    private Integer journalTarget;
    private Integer conferenceTarget;
    private Integer patentTarget;
    private Integer bookChapterTarget;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


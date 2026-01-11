package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true, sparse = true)
    private String email; // For FACULTY and ADMIN, null for STUDENT
    
    @Indexed(unique = true, sparse = true)
    private String registerNumber; // For STUDENT, null for FACULTY and ADMIN
    
    private String password;
    private String role; // FACULTY, ADMIN, or STUDENT
    
    private String facultyId; // Reference to FacultyProfile, null for ADMIN and STUDENT
    private String studentId; // Reference to StudentProfile, null for FACULTY and ADMIN
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


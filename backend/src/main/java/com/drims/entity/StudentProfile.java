package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String registerNumber;
    
    private String name;
    private String department;
    private String program; // B.Tech, M.Tech, Ph.D., etc.
    private String year; // Current year/class
    private String guideId; // Reference to FacultyProfile (guide/supervisor)
    private String guideName; // Name of the guide
    
    private String userId; // Reference to User
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

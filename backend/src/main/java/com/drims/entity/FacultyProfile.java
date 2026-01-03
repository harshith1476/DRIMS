package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "faculty_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyProfile {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String employeeId;
    
    private String name;
    private String designation;
    private String department;
    private List<String> researchAreas;
    
    private String orcidId;
    private String scopusId;
    private String googleScholarLink;
    
    @Indexed(unique = true)
    private String email;
    
    private String userId; // Reference to User
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


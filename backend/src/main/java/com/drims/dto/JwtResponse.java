package com.drims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email; // For FACULTY/ADMIN - email, null for STUDENT
    private String registerNumber; // For STUDENT - registerNumber, null for FACULTY/ADMIN
    private String role;
    private String facultyId; // null for ADMIN and STUDENT
    private String studentId; // null for FACULTY and ADMIN
}


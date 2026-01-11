package com.drims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email; // For FACULTY and ADMIN login
    private String registerNumber; // For STUDENT login (alternative to email)
    
    private String password;
    private String loginType; // "faculty", "admin", or "student" - optional, can be inferred
}


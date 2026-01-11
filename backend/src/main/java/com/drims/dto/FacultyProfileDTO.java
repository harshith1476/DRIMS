package com.drims.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyProfileDTO {
    private String id;
    
    @NotBlank(message = "Employee ID is required")
    private String employeeId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Designation is required")
    private String designation;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    private List<String> researchAreas;
    private String orcidId;
    private String scopusId;
    private String googleScholarLink;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}


package com.drims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatentDTO {
    private String id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String patentNumber;
    
    @NotBlank(message = "Inventors is required")
    private String inventors;
    
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;
    
    private String country;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String proofDocumentPath;
}


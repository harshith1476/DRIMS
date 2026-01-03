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
public class JournalDTO {
    private String id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Journal name is required")
    private String journalName;
    
    @NotBlank(message = "Authors is required")
    private String authors;
    
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;
    
    private String volume;
    private String issue;
    private String pages;
    private String doi;
    private String impactFactor;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String proofDocumentPath;
}


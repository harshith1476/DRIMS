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
    
    private String category; // National or International
    private String applicationNumber; // Patent application number
    private String filingDate; // Filing date
    
    // Approval workflow
    private String approvalStatus;
    private String remarks;
    
    // File uploads (conditional based on status)
    private String filingProofPath;
    private String publicationCertificatePath;
    private String grantCertificatePath;
    private String proofDocumentPath; // Legacy field
}


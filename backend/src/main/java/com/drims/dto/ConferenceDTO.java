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
public class ConferenceDTO {
    private String id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Conference name is required")
    private String conferenceName;
    
    @NotBlank(message = "Authors is required")
    private String authors;
    
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;
    
    private String location;
    private String date;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    // New fields for enhanced conference
    private String organizer;
    private String category; // National or International
    private String registrationAmount;
    private String paymentMode; // Cash, Online, Cheque, etc.
    
    // Student participation fields
    private String studentName;
    private String studentRegisterNumber;
    private String guideId;
    private String guideName;
    
    // Approval workflow
    private String approvalStatus;
    private String remarks;
    
    // File uploads
    private String registrationReceiptPath;
    private String certificatePath;
    private String proofDocumentPath; // Legacy field
}


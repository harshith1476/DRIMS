package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "patents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patent {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile
    
    private String title;
    private String applicationNumber; // Patent application number
    private String filingDate; // Filing date
    private String patentNumber; // Patent number (if granted)
    private String inventors;
    private Integer year;
    private String country;
    private String status; // Filed, Published, Granted (status flow)
    private String category; // National or International
    
    // Approval workflow
    private String approvalStatus; // SUBMITTED, APPROVED, REJECTED, SENT_BACK, LOCKED
    private String remarks; // Admin remarks on rejection/send back
    private String approvedBy; // Admin user ID who approved
    private LocalDateTime approvedAt; // Approval timestamp
    
    // File uploads (mandatory, conditional based on status)
    private String filingProofPath; // PDF file path (required for Filed status)
    private String publicationCertificatePath; // PDF file path (required for Published status)
    private String grantCertificatePath; // PDF file path (required for Granted status)
    
    // Legacy field for backward compatibility (deprecated, use specific fields above)
    @Deprecated
    private String proofDocumentPath; // PDF file path - kept for backward compatibility
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


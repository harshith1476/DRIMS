package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "journals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Journal {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile, null for student publications
    private String studentId; // Reference to StudentProfile, null for faculty publications
    
    private String title;
    private String journalName;
    private String authors; // Author 1 (mandatory)
    private String author2; // Optional
    private String author3; // Optional
    private String author4; // Optional
    private String author5; // Optional
    private String author6; // Optional
    
    private Integer year;
    private String volume;
    private String issue;
    private String pages;
    private String doi;
    private String impactFactor;
    private String status; // Published, Accepted, Submitted
    private String category; // National or International
    private String indexType; // SCI, SCIE, Scopus, ESCI, Web of Science (WoS), UGC CARE
    private String publisher;
    private String issn;
    private String openAccess; // Open Access or Subscription
    
    // Approval workflow
    private String approvalStatus; // SUBMITTED, APPROVED, REJECTED, SENT_BACK, LOCKED
    private String remarks; // Admin remarks on rejection/send back
    private String approvedBy; // Admin user ID who approved
    private LocalDateTime approvedAt; // Approval timestamp
    
    // File uploads (mandatory)
    private String acceptanceMailPath; // PDF file path
    private String publishedPaperPath; // PDF file path
    private String indexProofPath; // PDF file path
    
    // Legacy field for backward compatibility (deprecated, use specific fields above)
    @Deprecated
    private String proofDocumentPath; // PDF file path - kept for backward compatibility
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


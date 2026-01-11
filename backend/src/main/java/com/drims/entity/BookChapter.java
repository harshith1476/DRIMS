package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "book_chapters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookChapter {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile
    
    private String title; // Chapter Title
    private String bookTitle;
    private String authors;
    private String editors;
    private String publisher;
    private Integer year;
    private String pages; // Page numbers (e.g., "45-67")
    private String isbn;
    private String status; // Published, Accepted, Submitted
    private String category; // National or International
    
    // Approval workflow
    private String approvalStatus; // SUBMITTED, APPROVED, REJECTED, SENT_BACK, LOCKED
    private String remarks; // Admin remarks on rejection/send back
    private String approvedBy; // Admin user ID who approved
    private LocalDateTime approvedAt; // Approval timestamp
    
    // File uploads (mandatory)
    private String chapterPdfPath; // PDF file path
    private String isbnProofPath; // PDF file path
    
    // Legacy field for backward compatibility (deprecated, use specific fields above)
    @Deprecated
    private String proofDocumentPath; // PDF file path - kept for backward compatibility
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


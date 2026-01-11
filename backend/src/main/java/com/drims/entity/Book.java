package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile
    
    private String bookTitle;
    private String publisher;
    private String isbn;
    private Integer publicationYear;
    private String role; // Author or Editor
    private String category; // National or International
    private String status; // Published, Accepted, Submitted
    
    // Approval workflow
    private String approvalStatus; // SUBMITTED, APPROVED, REJECTED, SENT_BACK, LOCKED
    private String remarks; // Admin remarks on rejection/send back
    private String approvedBy; // Admin user ID who approved
    private LocalDateTime approvedAt; // Approval timestamp
    
    // File uploads
    private String bookCoverPath; // PDF file path
    private String isbnProofPath; // PDF file path
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.drims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String id;
    
    @NotBlank(message = "Book title is required")
    private String bookTitle;
    
    @NotBlank(message = "Publisher is required")
    private String publisher;
    
    @NotBlank(message = "ISBN is required")
    private String isbn;
    
    @NotNull(message = "Publication year is required")
    private Integer publicationYear;
    
    @NotBlank(message = "Role is required")
    private String role; // Author or Editor
    
    @NotBlank(message = "Category is required")
    private String category; // National or International
    
    @NotBlank(message = "Status is required")
    private String status; // Published, Accepted, Submitted
    
    // Approval workflow
    private String approvalStatus; // SUBMITTED, APPROVED, REJECTED, SENT_BACK, LOCKED
    private String remarks;
    
    // File uploads
    private String bookCoverPath;
    private String isbnProofPath;
}

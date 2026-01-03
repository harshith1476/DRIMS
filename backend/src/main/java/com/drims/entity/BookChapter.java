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
    
    private String title;
    private String bookTitle;
    private String authors;
    private String editors;
    private String publisher;
    private Integer year;
    private String pages;
    private String isbn;
    private String status; // Published, Accepted, Submitted
    
    private String proofDocumentPath; // PDF file path
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


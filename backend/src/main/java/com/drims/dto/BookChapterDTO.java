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
public class BookChapterDTO {
    private String id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Book title is required")
    private String bookTitle;
    
    @NotBlank(message = "Authors is required")
    private String authors;
    
    private String editors;
    private String publisher;
    
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;
    
    private String pages;
    private String isbn;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String proofDocumentPath;
}


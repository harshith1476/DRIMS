package com.drims.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalActionDTO {
    @NotBlank(message = "Action is required")
    private String action; // APPROVE, REJECT, SEND_BACK, LOCK
    
    private String remarks; // Required for REJECT and optional for SEND_BACK
    
    private String publicationType; // JOURNAL, CONFERENCE, BOOK, BOOK_CHAPTER, PATENT
    
    private String publicationId;
}

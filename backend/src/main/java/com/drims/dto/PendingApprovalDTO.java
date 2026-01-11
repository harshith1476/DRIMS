package com.drims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingApprovalDTO {
    private String id;
    private String publicationType; // JOURNAL, CONFERENCE, BOOK, BOOK_CHAPTER, PATENT
    private String title;
    private String facultyId;
    private String facultyName;
    private String studentId;
    private String studentName;
    private String approvalStatus; // SUBMITTED, SENT_BACK
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
}

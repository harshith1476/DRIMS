package com.drims.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "conferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conference {
    @Id
    private String id;
    
    private String facultyId; // Reference to FacultyProfile, null for student publications
    private String studentId; // Reference to StudentProfile, null for faculty publications
    
    private String title;
    private String conferenceName;
    private String organizer;
    private String authors;
    private Integer year;
    private String location;
    private String date;
    private String status; // Published, Accepted, Submitted, Communicated
    private String category; // National or International
    private String registrationAmount;
    private String paymentMode; // Cash, Online, Cheque, etc.
    
    // Student participation fields (optional)
    private Boolean isStudentPublication; // true if student publication
    private String studentName; // Student name if student publication
    private String studentRegisterNumber; // Student register number
    private String guideId; // Reference to FacultyProfile (guide/supervisor)
    private String guideName; // Name of the guide
    
    // Approval workflow
    private String approvalStatus; // SUBMITTED, APPROVED, REJECTED, SENT_BACK, LOCKED
    private String remarks; // Admin remarks on rejection/send back
    private String approvedBy; // Admin user ID who approved
    private LocalDateTime approvedAt; // Approval timestamp
    
    // File uploads (mandatory)
    private String registrationReceiptPath; // PDF file path
    private String certificatePath; // PDF file path
    
    // Legacy field for backward compatibility (deprecated, use specific fields above)
    @Deprecated
    private String proofDocumentPath; // PDF file path - kept for backward compatibility
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


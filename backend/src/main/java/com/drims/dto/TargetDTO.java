package com.drims.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetDTO {
    private String id;
    
    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be valid")
    private Integer year;
    
    @NotNull(message = "Journal target is required")
    @Min(value = 0, message = "Target must be non-negative")
    private Integer journalTarget;
    
    @NotNull(message = "Conference target is required")
    @Min(value = 0, message = "Target must be non-negative")
    private Integer conferenceTarget;
    
    @NotNull(message = "Patent target is required")
    @Min(value = 0, message = "Target must be non-negative")
    private Integer patentTarget;
    
    @NotNull(message = "Book chapter target is required")
    @Min(value = 0, message = "Target must be non-negative")
    private Integer bookChapterTarget;
}


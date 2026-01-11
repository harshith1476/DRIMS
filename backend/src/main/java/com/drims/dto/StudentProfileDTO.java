package com.drims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileDTO {
    private String id;
    private String registerNumber;
    private String name;
    private String department;
    private String program;
    private String year;
    private String guideId;
    private String guideName;
}

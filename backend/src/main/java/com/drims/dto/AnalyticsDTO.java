package com.drims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    private Map<Integer, Integer> yearWiseTotals;
    private Map<String, Integer> categoryWiseTotals;
    private Map<String, Integer> facultyWiseContribution;
    private Map<String, Integer> statusWiseBreakdown;
}


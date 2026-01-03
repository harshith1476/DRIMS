package com.drims.service;

import com.drims.dto.AnalyticsDTO;
import com.drims.entity.*;
import com.drims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    
    @Autowired
    private JournalRepository journalRepository;
    
    @Autowired
    private ConferenceRepository conferenceRepository;
    
    @Autowired
    private PatentRepository patentRepository;
    
    @Autowired
    private BookChapterRepository bookChapterRepository;
    
    @Autowired
    private FacultyProfileRepository facultyProfileRepository;
    
    public AnalyticsDTO getAnalytics() {
        AnalyticsDTO analytics = new AnalyticsDTO();
        
        // Year-wise totals
        Map<Integer, Integer> yearWise = new HashMap<>();
        addYearWiseCounts(journalRepository.findAll(), yearWise);
        addYearWiseCounts(conferenceRepository.findAll(), yearWise);
        addYearWiseCounts(patentRepository.findAll(), yearWise);
        addYearWiseCounts(bookChapterRepository.findAll(), yearWise);
        analytics.setYearWiseTotals(yearWise);
        
        // Category-wise totals
        Map<String, Integer> categoryWise = new HashMap<>();
        categoryWise.put("Journals", (int) journalRepository.count());
        categoryWise.put("Conferences", (int) conferenceRepository.count());
        categoryWise.put("Patents", (int) patentRepository.count());
        categoryWise.put("Book Chapters", (int) bookChapterRepository.count());
        analytics.setCategoryWiseTotals(categoryWise);
        
        // Faculty-wise contribution
        Map<String, Integer> facultyWise = new HashMap<>();
        List<FacultyProfile> allFaculty = facultyProfileRepository.findAll();
        for (FacultyProfile faculty : allFaculty) {
            int count = 0;
            count += journalRepository.findByFacultyId(faculty.getId()).size();
            count += conferenceRepository.findByFacultyId(faculty.getId()).size();
            count += patentRepository.findByFacultyId(faculty.getId()).size();
            count += bookChapterRepository.findByFacultyId(faculty.getId()).size();
            if (count > 0) {
                facultyWise.put(faculty.getName(), count);
            }
        }
        analytics.setFacultyWiseContribution(facultyWise);
        
        // Status-wise breakdown
        Map<String, Integer> statusWise = new HashMap<>();
        addStatusCounts(journalRepository.findAll(), statusWise);
        addStatusCounts(conferenceRepository.findAll(), statusWise);
        addStatusCounts(patentRepository.findAll(), statusWise);
        addStatusCounts(bookChapterRepository.findAll(), statusWise);
        analytics.setStatusWiseBreakdown(statusWise);
        
        return analytics;
    }
    
    private void addYearWiseCounts(List<?> items, Map<Integer, Integer> yearWise) {
        for (Object item : items) {
            Integer year = null;
            if (item instanceof Journal) {
                year = ((Journal) item).getYear();
            } else if (item instanceof Conference) {
                year = ((Conference) item).getYear();
            } else if (item instanceof Patent) {
                year = ((Patent) item).getYear();
            } else if (item instanceof BookChapter) {
                year = ((BookChapter) item).getYear();
            }
            if (year != null) {
                yearWise.put(year, yearWise.getOrDefault(year, 0) + 1);
            }
        }
    }
    
    private void addStatusCounts(List<?> items, Map<String, Integer> statusWise) {
        for (Object item : items) {
            String status = null;
            if (item instanceof Journal) {
                status = ((Journal) item).getStatus();
            } else if (item instanceof Conference) {
                status = ((Conference) item).getStatus();
            } else if (item instanceof Patent) {
                status = ((Patent) item).getStatus();
            } else if (item instanceof BookChapter) {
                status = ((BookChapter) item).getStatus();
            }
            if (status != null) {
                statusWise.put(status, statusWise.getOrDefault(status, 0) + 1);
            }
        }
    }
}


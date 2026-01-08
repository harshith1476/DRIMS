package com.drims.controller;

import com.drims.dto.*;
import com.drims.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private FacultyProfileService facultyProfileService;
    
    @Autowired
    private TargetService targetService;
    
    @Autowired
    private JournalService journalService;
    
    @Autowired
    private ConferenceService conferenceService;
    
    @Autowired
    private PatentService patentService;
    
    @Autowired
    private BookChapterService bookChapterService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    // Faculty Profiles (Read-only)
    @GetMapping("/faculty-profiles")
    public ResponseEntity<List<FacultyProfileDTO>> getAllProfiles() {
        List<FacultyProfileDTO> profiles = facultyProfileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }
    
    @GetMapping("/faculty-profiles/{id}")
    public ResponseEntity<FacultyProfileDTO> getProfileById(@PathVariable String id) {
        FacultyProfileDTO profile = facultyProfileService.getProfileById(id);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/faculty-profiles/{id}/complete")
    public ResponseEntity<FacultyCompleteDataDTO> getCompleteFacultyData(@PathVariable String id) {
        FacultyCompleteDataDTO completeData = new FacultyCompleteDataDTO();
        completeData.setProfile(facultyProfileService.getProfileById(id));
        completeData.setTargets(targetService.getTargetsByFaculty(id));
        completeData.setJournals(journalService.getJournalsByFaculty(id));
        completeData.setConferences(conferenceService.getConferencesByFaculty(id));
        completeData.setPatents(patentService.getPatentsByFaculty(id));
        completeData.setBookChapters(bookChapterService.getBookChaptersByFaculty(id));
        return ResponseEntity.ok(completeData);
    }
    
    // All Targets
    @GetMapping("/targets")
    public ResponseEntity<List<TargetDTO>> getAllTargets() {
        List<TargetDTO> targets = targetService.getAllTargets();
        return ResponseEntity.ok(targets);
    }
    
    // All Publications
    @GetMapping("/journals")
    public ResponseEntity<List<JournalDTO>> getAllJournals() {
        List<JournalDTO> journals = journalService.getAllJournals();
        return ResponseEntity.ok(journals);
    }
    
    @GetMapping("/conferences")
    public ResponseEntity<List<ConferenceDTO>> getAllConferences() {
        List<ConferenceDTO> conferences = conferenceService.getAllConferences();
        return ResponseEntity.ok(conferences);
    }
    
    @GetMapping("/patents")
    public ResponseEntity<List<PatentDTO>> getAllPatents() {
        List<PatentDTO> patents = patentService.getAllPatents();
        return ResponseEntity.ok(patents);
    }
    
    @GetMapping("/book-chapters")
    public ResponseEntity<List<BookChapterDTO>> getAllBookChapters() {
        List<BookChapterDTO> bookChapters = bookChapterService.getAllBookChapters();
        return ResponseEntity.ok(bookChapters);
    }
    
    // Analytics
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        AnalyticsDTO analytics = analyticsService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    // Excel Export
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String category) {
        try {
            byte[] excelData = excelExportService.exportToExcel(year, category);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "research_data.xlsx");
            headers.setAccessControlExposeHeaders(List.of("Content-Disposition"));
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}


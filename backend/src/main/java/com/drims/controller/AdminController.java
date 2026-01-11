package com.drims.controller;

import com.drims.dto.*;
import com.drims.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    private BookService bookService;
    
    @Autowired
    private AdminApprovalService adminApprovalService;
    
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
    
    @GetMapping("/books")
    public ResponseEntity<List<com.drims.dto.BookDTO>> getAllBooks() {
        List<com.drims.dto.BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    
    // Approval Workflow
    @GetMapping("/approvals/pending")
    public ResponseEntity<List<PendingApprovalDTO>> getPendingApprovals(
            @RequestParam(required = false) String type) {
        List<PendingApprovalDTO> pending = adminApprovalService.getPendingApprovals(type);
        return ResponseEntity.ok(pending);
    }
    
    @PostMapping("/approvals/{type}/{id}/approve")
    public ResponseEntity<Void> approvePublication(
            Authentication authentication,
            @PathVariable String type,
            @PathVariable String id) {
        String adminId = authentication.getName(); // Admin email/ID
        adminApprovalService.approvePublication(type, id, adminId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/approvals/{type}/{id}/reject")
    public ResponseEntity<Void> rejectPublication(
            Authentication authentication,
            @PathVariable String type,
            @PathVariable String id,
            @Valid @RequestBody ApprovalActionDTO actionDto) {
        String adminId = authentication.getName();
        if (actionDto.getRemarks() == null || actionDto.getRemarks().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        adminApprovalService.rejectPublication(type, id, adminId, actionDto.getRemarks());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/approvals/{type}/{id}/send-back")
    public ResponseEntity<Void> sendBackPublication(
            Authentication authentication,
            @PathVariable String type,
            @PathVariable String id,
            @RequestBody(required = false) ApprovalActionDTO actionDto) {
        String adminId = authentication.getName();
        String remarks = actionDto != null ? actionDto.getRemarks() : null;
        adminApprovalService.sendBackPublication(type, id, adminId, remarks);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/approvals/{type}/{id}/lock")
    public ResponseEntity<Void> lockPublication(
            Authentication authentication,
            @PathVariable String type,
            @PathVariable String id) {
        String adminId = authentication.getName();
        adminApprovalService.lockPublication(type, id, adminId);
        return ResponseEntity.ok().build();
    }
    
    // Analytics
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        AnalyticsDTO analytics = analyticsService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }
    
    @Autowired
    private ReportService reportService;
    
    // Reports Generation
    @GetMapping("/reports/naac")
    public ResponseEntity<Map<String, Object>> generateNAACReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String facultyId) {
        Map<String, Object> report = reportService.generateNAACReport(year, facultyId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/nba")
    public ResponseEntity<Map<String, Object>> generateNBAReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String facultyId) {
        Map<String, Object> report = reportService.generateNBAReport(year, facultyId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/nirf")
    public ResponseEntity<Map<String, Object>> generateNIRFReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String facultyId) {
        Map<String, Object> report = reportService.generateNIRFReport(year, facultyId);
        return ResponseEntity.ok(report);
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
    
    // Export Reports to Excel/PDF
    @PostMapping("/reports/export/excel")
    public ResponseEntity<byte[]> exportReportToExcel(
            @RequestParam String reportType, // NAAC, NBA, NIRF
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String facultyId) {
        try {
            Map<String, Object> reportData;
            switch (reportType.toUpperCase()) {
                case "NAAC":
                    reportData = reportService.generateNAACReport(year, facultyId);
                    break;
                case "NBA":
                    reportData = reportService.generateNBAReport(year, facultyId);
                    break;
                case "NIRF":
                    reportData = reportService.generateNIRFReport(year, facultyId);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Use existing ExcelExportService to export report
            byte[] excelData = excelExportService.exportReportToExcel(reportData, reportType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", reportType.toLowerCase() + "_report.xlsx");
            headers.setAccessControlExposeHeaders(List.of("Content-Disposition"));
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/reports/export/pdf")
    public ResponseEntity<byte[]> exportReportToPDF(
            @RequestParam String reportType, // NAAC, NBA, NIRF
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String facultyId) {
        try {
            Map<String, Object> reportData;
            switch (reportType.toUpperCase()) {
                case "NAAC":
                    reportData = reportService.generateNAACReport(year, facultyId);
                    break;
                case "NBA":
                    reportData = reportService.generateNBAReport(year, facultyId);
                    break;
                case "NIRF":
                    reportData = reportService.generateNIRFReport(year, facultyId);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // PDF export would require a PDF generation library (iText, Apache PDFBox, etc.)
            // For now, return a placeholder response
            byte[] pdfData = generatePDFFromReport(reportData, reportType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", reportType.toLowerCase() + "_report.pdf");
            headers.setAccessControlExposeHeaders(List.of("Content-Disposition"));
            
            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private byte[] generatePDFFromReport(Map<String, Object> reportData, String reportType) {
        // Placeholder - PDF generation would be implemented here
        // Would require PDF library (iText, Apache PDFBox, etc.)
        return new byte[0]; // Return empty for now
    }
}


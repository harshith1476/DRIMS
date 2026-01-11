package com.drims.service;

import com.drims.dto.ApprovalActionDTO;
import com.drims.dto.PendingApprovalDTO;
import com.drims.entity.*;
import com.drims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminApprovalService {
    
    @Autowired
    private JournalRepository journalRepository;
    
    @Autowired
    private ConferenceRepository conferenceRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BookChapterRepository bookChapterRepository;
    
    @Autowired
    private PatentRepository patentRepository;
    
    @Autowired
    private FacultyProfileRepository facultyProfileRepository;
    
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    
    // Get all pending approvals
    public List<PendingApprovalDTO> getPendingApprovals(String type) {
        List<PendingApprovalDTO> pendingList = new ArrayList<>();
        
        List<String> pendingStatuses = Arrays.asList("SUBMITTED", "SENT_BACK");
        
        if (type == null || "JOURNAL".equalsIgnoreCase(type)) {
            journalRepository.findByApprovalStatusIn(pendingStatuses).forEach(journal -> {
                pendingList.add(convertJournalToPending(journal));
            });
        }
        
        if (type == null || "CONFERENCE".equalsIgnoreCase(type)) {
            conferenceRepository.findByApprovalStatusIn(pendingStatuses).forEach(conference -> {
                pendingList.add(convertConferenceToPending(conference));
            });
        }
        
        if (type == null || "BOOK".equalsIgnoreCase(type)) {
            bookRepository.findByApprovalStatusIn(pendingStatuses).forEach(book -> {
                pendingList.add(convertBookToPending(book));
            });
        }
        
        if (type == null || "BOOK_CHAPTER".equalsIgnoreCase(type)) {
            bookChapterRepository.findByApprovalStatusIn(pendingStatuses).forEach(bookChapter -> {
                pendingList.add(convertBookChapterToPending(bookChapter));
            });
        }
        
        if (type == null || "PATENT".equalsIgnoreCase(type)) {
            patentRepository.findByApprovalStatusIn(pendingStatuses).forEach(patent -> {
                pendingList.add(convertPatentToPending(patent));
            });
        }
        
        return pendingList;
    }
    
    // Approve publication
    public void approvePublication(String type, String id, String adminId) {
        switch (type.toUpperCase()) {
            case "JOURNAL":
                approveJournal(id, adminId);
                break;
            case "CONFERENCE":
                approveConference(id, adminId);
                break;
            case "BOOK":
                approveBook(id, adminId);
                break;
            case "BOOK_CHAPTER":
                approveBookChapter(id, adminId);
                break;
            case "PATENT":
                approvePatent(id, adminId);
                break;
            default:
                throw new RuntimeException("Invalid publication type: " + type);
        }
    }
    
    // Reject publication (requires remarks)
    public void rejectPublication(String type, String id, String adminId, String remarks) {
        if (remarks == null || remarks.trim().isEmpty()) {
            throw new RuntimeException("Remarks are required for rejection");
        }
        
        switch (type.toUpperCase()) {
            case "JOURNAL":
                rejectJournal(id, adminId, remarks);
                break;
            case "CONFERENCE":
                rejectConference(id, adminId, remarks);
                break;
            case "BOOK":
                rejectBook(id, adminId, remarks);
                break;
            case "BOOK_CHAPTER":
                rejectBookChapter(id, adminId, remarks);
                break;
            case "PATENT":
                rejectPatent(id, adminId, remarks);
                break;
            default:
                throw new RuntimeException("Invalid publication type: " + type);
        }
    }
    
    // Send back publication (optional remarks)
    public void sendBackPublication(String type, String id, String adminId, String remarks) {
        switch (type.toUpperCase()) {
            case "JOURNAL":
                sendBackJournal(id, adminId, remarks);
                break;
            case "CONFERENCE":
                sendBackConference(id, adminId, remarks);
                break;
            case "BOOK":
                sendBackBook(id, adminId, remarks);
                break;
            case "BOOK_CHAPTER":
                sendBackBookChapter(id, adminId, remarks);
                break;
            case "PATENT":
                sendBackPatent(id, adminId, remarks);
                break;
            default:
                throw new RuntimeException("Invalid publication type: " + type);
        }
    }
    
    // Lock approved publication
    public void lockPublication(String type, String id, String adminId) {
        switch (type.toUpperCase()) {
            case "JOURNAL":
                lockJournal(id, adminId);
                break;
            case "CONFERENCE":
                lockConference(id, adminId);
                break;
            case "BOOK":
                lockBook(id, adminId);
                break;
            case "BOOK_CHAPTER":
                lockBookChapter(id, adminId);
                break;
            case "PATENT":
                lockPatent(id, adminId);
                break;
            default:
                throw new RuntimeException("Invalid publication type: " + type);
        }
    }
    
    // Journal approval methods
    private void approveJournal(String id, String adminId) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal not found"));
        journal.setApprovalStatus("APPROVED");
        journal.setApprovedBy(adminId);
        journal.setApprovedAt(LocalDateTime.now());
        journal.setRemarks(null);
        journal.setUpdatedAt(LocalDateTime.now());
        journalRepository.save(journal);
    }
    
    private void rejectJournal(String id, String adminId, String remarks) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal not found"));
        journal.setApprovalStatus("REJECTED");
        journal.setApprovedBy(adminId);
        journal.setApprovedAt(LocalDateTime.now());
        journal.setRemarks(remarks);
        journal.setUpdatedAt(LocalDateTime.now());
        journalRepository.save(journal);
    }
    
    private void sendBackJournal(String id, String adminId, String remarks) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal not found"));
        journal.setApprovalStatus("SENT_BACK");
        journal.setApprovedBy(adminId);
        journal.setApprovedAt(LocalDateTime.now());
        journal.setRemarks(remarks);
        journal.setUpdatedAt(LocalDateTime.now());
        journalRepository.save(journal);
    }
    
    private void lockJournal(String id, String adminId) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal not found"));
        if (!"APPROVED".equals(journal.getApprovalStatus())) {
            throw new RuntimeException("Only approved publications can be locked");
        }
        journal.setApprovalStatus("LOCKED");
        journal.setUpdatedAt(LocalDateTime.now());
        journalRepository.save(journal);
    }
    
    // Conference approval methods
    private void approveConference(String id, String adminId) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        conference.setApprovalStatus("APPROVED");
        conference.setApprovedBy(adminId);
        conference.setApprovedAt(LocalDateTime.now());
        conference.setRemarks(null);
        conference.setUpdatedAt(LocalDateTime.now());
        conferenceRepository.save(conference);
    }
    
    private void rejectConference(String id, String adminId, String remarks) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        conference.setApprovalStatus("REJECTED");
        conference.setApprovedBy(adminId);
        conference.setApprovedAt(LocalDateTime.now());
        conference.setRemarks(remarks);
        conference.setUpdatedAt(LocalDateTime.now());
        conferenceRepository.save(conference);
    }
    
    private void sendBackConference(String id, String adminId, String remarks) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        conference.setApprovalStatus("SENT_BACK");
        conference.setApprovedBy(adminId);
        conference.setApprovedAt(LocalDateTime.now());
        conference.setRemarks(remarks);
        conference.setUpdatedAt(LocalDateTime.now());
        conferenceRepository.save(conference);
    }
    
    private void lockConference(String id, String adminId) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        if (!"APPROVED".equals(conference.getApprovalStatus())) {
            throw new RuntimeException("Only approved publications can be locked");
        }
        conference.setApprovalStatus("LOCKED");
        conference.setUpdatedAt(LocalDateTime.now());
        conferenceRepository.save(conference);
    }
    
    // Book approval methods
    private void approveBook(String id, String adminId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setApprovalStatus("APPROVED");
        book.setApprovedBy(adminId);
        book.setApprovedAt(LocalDateTime.now());
        book.setRemarks(null);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }
    
    private void rejectBook(String id, String adminId, String remarks) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setApprovalStatus("REJECTED");
        book.setApprovedBy(adminId);
        book.setApprovedAt(LocalDateTime.now());
        book.setRemarks(remarks);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }
    
    private void sendBackBook(String id, String adminId, String remarks) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setApprovalStatus("SENT_BACK");
        book.setApprovedBy(adminId);
        book.setApprovedAt(LocalDateTime.now());
        book.setRemarks(remarks);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }
    
    private void lockBook(String id, String adminId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        if (!"APPROVED".equals(book.getApprovalStatus())) {
            throw new RuntimeException("Only approved publications can be locked");
        }
        book.setApprovalStatus("LOCKED");
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }
    
    // BookChapter approval methods
    private void approveBookChapter(String id, String adminId) {
        BookChapter bookChapter = bookChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookChapter not found"));
        bookChapter.setApprovalStatus("APPROVED");
        bookChapter.setApprovedBy(adminId);
        bookChapter.setApprovedAt(LocalDateTime.now());
        bookChapter.setRemarks(null);
        bookChapter.setUpdatedAt(LocalDateTime.now());
        bookChapterRepository.save(bookChapter);
    }
    
    private void rejectBookChapter(String id, String adminId, String remarks) {
        BookChapter bookChapter = bookChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookChapter not found"));
        bookChapter.setApprovalStatus("REJECTED");
        bookChapter.setApprovedBy(adminId);
        bookChapter.setApprovedAt(LocalDateTime.now());
        bookChapter.setRemarks(remarks);
        bookChapter.setUpdatedAt(LocalDateTime.now());
        bookChapterRepository.save(bookChapter);
    }
    
    private void sendBackBookChapter(String id, String adminId, String remarks) {
        BookChapter bookChapter = bookChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookChapter not found"));
        bookChapter.setApprovalStatus("SENT_BACK");
        bookChapter.setApprovedBy(adminId);
        bookChapter.setApprovedAt(LocalDateTime.now());
        bookChapter.setRemarks(remarks);
        bookChapter.setUpdatedAt(LocalDateTime.now());
        bookChapterRepository.save(bookChapter);
    }
    
    private void lockBookChapter(String id, String adminId) {
        BookChapter bookChapter = bookChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookChapter not found"));
        if (!"APPROVED".equals(bookChapter.getApprovalStatus())) {
            throw new RuntimeException("Only approved publications can be locked");
        }
        bookChapter.setApprovalStatus("LOCKED");
        bookChapter.setUpdatedAt(LocalDateTime.now());
        bookChapterRepository.save(bookChapter);
    }
    
    // Patent approval methods
    private void approvePatent(String id, String adminId) {
        Patent patent = patentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patent not found"));
        patent.setApprovalStatus("APPROVED");
        patent.setApprovedBy(adminId);
        patent.setApprovedAt(LocalDateTime.now());
        patent.setRemarks(null);
        patent.setUpdatedAt(LocalDateTime.now());
        patentRepository.save(patent);
    }
    
    private void rejectPatent(String id, String adminId, String remarks) {
        Patent patent = patentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patent not found"));
        patent.setApprovalStatus("REJECTED");
        patent.setApprovedBy(adminId);
        patent.setApprovedAt(LocalDateTime.now());
        patent.setRemarks(remarks);
        patent.setUpdatedAt(LocalDateTime.now());
        patentRepository.save(patent);
    }
    
    private void sendBackPatent(String id, String adminId, String remarks) {
        Patent patent = patentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patent not found"));
        patent.setApprovalStatus("SENT_BACK");
        patent.setApprovedBy(adminId);
        patent.setApprovedAt(LocalDateTime.now());
        patent.setRemarks(remarks);
        patent.setUpdatedAt(LocalDateTime.now());
        patentRepository.save(patent);
    }
    
    private void lockPatent(String id, String adminId) {
        Patent patent = patentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patent not found"));
        if (!"APPROVED".equals(patent.getApprovalStatus())) {
            throw new RuntimeException("Only approved publications can be locked");
        }
        patent.setApprovalStatus("LOCKED");
        patent.setUpdatedAt(LocalDateTime.now());
        patentRepository.save(patent);
    }
    
    // Conversion methods to PendingApprovalDTO
    private PendingApprovalDTO convertJournalToPending(Journal journal) {
        PendingApprovalDTO dto = new PendingApprovalDTO();
        dto.setId(journal.getId());
        dto.setPublicationType("JOURNAL");
        dto.setTitle(journal.getTitle());
        dto.setFacultyId(journal.getFacultyId());
        dto.setStudentId(journal.getStudentId());
        if (journal.getFacultyId() != null) {
            facultyProfileRepository.findById(journal.getFacultyId())
                    .ifPresent(fp -> dto.setFacultyName(fp.getName()));
        }
        if (journal.getStudentId() != null) {
            studentProfileRepository.findById(journal.getStudentId())
                    .ifPresent(sp -> dto.setStudentName(sp.getName()));
        }
        dto.setApprovalStatus(journal.getApprovalStatus());
        dto.setSubmittedAt(journal.getCreatedAt());
        dto.setUpdatedAt(journal.getUpdatedAt());
        return dto;
    }
    
    private PendingApprovalDTO convertConferenceToPending(Conference conference) {
        PendingApprovalDTO dto = new PendingApprovalDTO();
        dto.setId(conference.getId());
        dto.setPublicationType("CONFERENCE");
        dto.setTitle(conference.getTitle());
        dto.setFacultyId(conference.getFacultyId());
        dto.setStudentId(conference.getStudentId());
        if (conference.getFacultyId() != null) {
            facultyProfileRepository.findById(conference.getFacultyId())
                    .ifPresent(fp -> dto.setFacultyName(fp.getName()));
        }
        if (conference.getStudentId() != null) {
            studentProfileRepository.findById(conference.getStudentId())
                    .ifPresent(sp -> dto.setStudentName(sp.getName()));
        }
        dto.setApprovalStatus(conference.getApprovalStatus());
        dto.setSubmittedAt(conference.getCreatedAt());
        dto.setUpdatedAt(conference.getUpdatedAt());
        return dto;
    }
    
    private PendingApprovalDTO convertBookToPending(Book book) {
        PendingApprovalDTO dto = new PendingApprovalDTO();
        dto.setId(book.getId());
        dto.setPublicationType("BOOK");
        dto.setTitle(book.getBookTitle());
        dto.setFacultyId(book.getFacultyId());
        if (book.getFacultyId() != null) {
            facultyProfileRepository.findById(book.getFacultyId())
                    .ifPresent(fp -> dto.setFacultyName(fp.getName()));
        }
        dto.setApprovalStatus(book.getApprovalStatus());
        dto.setSubmittedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        return dto;
    }
    
    private PendingApprovalDTO convertBookChapterToPending(BookChapter bookChapter) {
        PendingApprovalDTO dto = new PendingApprovalDTO();
        dto.setId(bookChapter.getId());
        dto.setPublicationType("BOOK_CHAPTER");
        dto.setTitle(bookChapter.getTitle());
        dto.setFacultyId(bookChapter.getFacultyId());
        if (bookChapter.getFacultyId() != null) {
            facultyProfileRepository.findById(bookChapter.getFacultyId())
                    .ifPresent(fp -> dto.setFacultyName(fp.getName()));
        }
        dto.setApprovalStatus(bookChapter.getApprovalStatus());
        dto.setSubmittedAt(bookChapter.getCreatedAt());
        dto.setUpdatedAt(bookChapter.getUpdatedAt());
        return dto;
    }
    
    private PendingApprovalDTO convertPatentToPending(Patent patent) {
        PendingApprovalDTO dto = new PendingApprovalDTO();
        dto.setId(patent.getId());
        dto.setPublicationType("PATENT");
        dto.setTitle(patent.getTitle());
        dto.setFacultyId(patent.getFacultyId());
        if (patent.getFacultyId() != null) {
            facultyProfileRepository.findById(patent.getFacultyId())
                    .ifPresent(fp -> dto.setFacultyName(fp.getName()));
        }
        dto.setApprovalStatus(patent.getApprovalStatus());
        dto.setSubmittedAt(patent.getCreatedAt());
        dto.setUpdatedAt(patent.getUpdatedAt());
        return dto;
    }
}

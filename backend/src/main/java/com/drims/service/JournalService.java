package com.drims.service;

import com.drims.dto.JournalDTO;
import com.drims.entity.Journal;
import com.drims.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalService {
    
    @Autowired
    private JournalRepository journalRepository;
    
    public JournalDTO createJournal(String facultyId, JournalDTO dto) {
        Journal journal = new Journal();
        journal.setFacultyId(facultyId);
        journal.setTitle(dto.getTitle());
        journal.setJournalName(dto.getJournalName());
        journal.setAuthors(dto.getAuthors());
        journal.setAuthor2(dto.getAuthor2());
        journal.setAuthor3(dto.getAuthor3());
        journal.setAuthor4(dto.getAuthor4());
        journal.setAuthor5(dto.getAuthor5());
        journal.setAuthor6(dto.getAuthor6());
        journal.setYear(dto.getYear());
        journal.setVolume(dto.getVolume());
        journal.setIssue(dto.getIssue());
        journal.setPages(dto.getPages());
        journal.setDoi(dto.getDoi());
        journal.setImpactFactor(dto.getImpactFactor());
        journal.setStatus(dto.getStatus());
        journal.setCategory(dto.getCategory());
        journal.setIndexType(dto.getIndexType());
        journal.setPublisher(dto.getPublisher());
        journal.setIssn(dto.getIssn());
        journal.setOpenAccess(dto.getOpenAccess());
        journal.setApprovalStatus("SUBMITTED");
        journal.setAcceptanceMailPath(dto.getAcceptanceMailPath());
        journal.setPublishedPaperPath(dto.getPublishedPaperPath());
        journal.setIndexProofPath(dto.getIndexProofPath());
        journal.setProofDocumentPath(dto.getProofDocumentPath()); // Legacy
        journal.setCreatedAt(LocalDateTime.now());
        journal.setUpdatedAt(LocalDateTime.now());
        
        journal = journalRepository.save(journal);
        return convertToDTO(journal);
    }
    
    public JournalDTO updateJournal(String id, String facultyId, JournalDTO dto) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal not found"));
        
        if (!journal.getFacultyId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to update this journal");
        }
        
        // Don't allow updates if already approved/locked
        if ("APPROVED".equals(journal.getApprovalStatus()) || "LOCKED".equals(journal.getApprovalStatus())) {
            throw new RuntimeException("Cannot update approved/locked journal");
        }
        
        journal.setTitle(dto.getTitle());
        journal.setJournalName(dto.getJournalName());
        journal.setAuthors(dto.getAuthors());
        journal.setAuthor2(dto.getAuthor2());
        journal.setAuthor3(dto.getAuthor3());
        journal.setAuthor4(dto.getAuthor4());
        journal.setAuthor5(dto.getAuthor5());
        journal.setAuthor6(dto.getAuthor6());
        journal.setYear(dto.getYear());
        journal.setVolume(dto.getVolume());
        journal.setIssue(dto.getIssue());
        journal.setPages(dto.getPages());
        journal.setDoi(dto.getDoi());
        journal.setImpactFactor(dto.getImpactFactor());
        journal.setStatus(dto.getStatus());
        journal.setCategory(dto.getCategory());
        journal.setIndexType(dto.getIndexType());
        journal.setPublisher(dto.getPublisher());
        journal.setIssn(dto.getIssn());
        journal.setOpenAccess(dto.getOpenAccess());
        if (dto.getAcceptanceMailPath() != null) {
            journal.setAcceptanceMailPath(dto.getAcceptanceMailPath());
        }
        if (dto.getPublishedPaperPath() != null) {
            journal.setPublishedPaperPath(dto.getPublishedPaperPath());
        }
        if (dto.getIndexProofPath() != null) {
            journal.setIndexProofPath(dto.getIndexProofPath());
        }
        if (dto.getProofDocumentPath() != null) {
            journal.setProofDocumentPath(dto.getProofDocumentPath()); // Legacy
        }
        journal.setUpdatedAt(LocalDateTime.now());
        
        journal = journalRepository.save(journal);
        return convertToDTO(journal);
    }
    
    public List<JournalDTO> getJournalsByFaculty(String facultyId) {
        return journalRepository.findByFacultyId(facultyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<JournalDTO> getAllJournals() {
        return journalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void deleteJournal(String id, String facultyId) {
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal not found"));
        
        if (!journal.getFacultyId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to delete this journal");
        }
        
        // Don't allow deletion if already approved/locked
        if ("APPROVED".equals(journal.getApprovalStatus()) || "LOCKED".equals(journal.getApprovalStatus())) {
            throw new RuntimeException("Cannot delete approved/locked journal");
        }
        
        journalRepository.delete(journal);
    }
    
    private JournalDTO convertToDTO(Journal journal) {
        JournalDTO dto = new JournalDTO();
        dto.setId(journal.getId());
        dto.setTitle(journal.getTitle());
        dto.setJournalName(journal.getJournalName());
        dto.setAuthors(journal.getAuthors());
        dto.setAuthor2(journal.getAuthor2());
        dto.setAuthor3(journal.getAuthor3());
        dto.setAuthor4(journal.getAuthor4());
        dto.setAuthor5(journal.getAuthor5());
        dto.setAuthor6(journal.getAuthor6());
        dto.setYear(journal.getYear());
        dto.setVolume(journal.getVolume());
        dto.setIssue(journal.getIssue());
        dto.setPages(journal.getPages());
        dto.setDoi(journal.getDoi());
        dto.setImpactFactor(journal.getImpactFactor());
        dto.setStatus(journal.getStatus());
        dto.setCategory(journal.getCategory());
        dto.setIndexType(journal.getIndexType());
        dto.setPublisher(journal.getPublisher());
        dto.setIssn(journal.getIssn());
        dto.setOpenAccess(journal.getOpenAccess());
        dto.setApprovalStatus(journal.getApprovalStatus());
        dto.setRemarks(journal.getRemarks());
        dto.setAcceptanceMailPath(journal.getAcceptanceMailPath());
        dto.setPublishedPaperPath(journal.getPublishedPaperPath());
        dto.setIndexProofPath(journal.getIndexProofPath());
        dto.setProofDocumentPath(journal.getProofDocumentPath()); // Legacy
        return dto;
    }
}


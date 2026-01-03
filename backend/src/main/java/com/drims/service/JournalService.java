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
        journal.setYear(dto.getYear());
        journal.setVolume(dto.getVolume());
        journal.setIssue(dto.getIssue());
        journal.setPages(dto.getPages());
        journal.setDoi(dto.getDoi());
        journal.setImpactFactor(dto.getImpactFactor());
        journal.setStatus(dto.getStatus());
        journal.setProofDocumentPath(dto.getProofDocumentPath());
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
        
        journal.setTitle(dto.getTitle());
        journal.setJournalName(dto.getJournalName());
        journal.setAuthors(dto.getAuthors());
        journal.setYear(dto.getYear());
        journal.setVolume(dto.getVolume());
        journal.setIssue(dto.getIssue());
        journal.setPages(dto.getPages());
        journal.setDoi(dto.getDoi());
        journal.setImpactFactor(dto.getImpactFactor());
        journal.setStatus(dto.getStatus());
        if (dto.getProofDocumentPath() != null) {
            journal.setProofDocumentPath(dto.getProofDocumentPath());
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
        
        journalRepository.delete(journal);
    }
    
    private JournalDTO convertToDTO(Journal journal) {
        JournalDTO dto = new JournalDTO();
        dto.setId(journal.getId());
        dto.setTitle(journal.getTitle());
        dto.setJournalName(journal.getJournalName());
        dto.setAuthors(journal.getAuthors());
        dto.setYear(journal.getYear());
        dto.setVolume(journal.getVolume());
        dto.setIssue(journal.getIssue());
        dto.setPages(journal.getPages());
        dto.setDoi(journal.getDoi());
        dto.setImpactFactor(journal.getImpactFactor());
        dto.setStatus(journal.getStatus());
        dto.setProofDocumentPath(journal.getProofDocumentPath());
        return dto;
    }
}


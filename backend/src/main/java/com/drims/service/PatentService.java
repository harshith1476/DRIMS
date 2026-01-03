package com.drims.service;

import com.drims.dto.PatentDTO;
import com.drims.entity.Patent;
import com.drims.repository.PatentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatentService {
    
    @Autowired
    private PatentRepository patentRepository;
    
    public PatentDTO createPatent(String facultyId, PatentDTO dto) {
        Patent patent = new Patent();
        patent.setFacultyId(facultyId);
        patent.setTitle(dto.getTitle());
        patent.setPatentNumber(dto.getPatentNumber());
        patent.setInventors(dto.getInventors());
        patent.setYear(dto.getYear());
        patent.setCountry(dto.getCountry());
        patent.setStatus(dto.getStatus());
        patent.setProofDocumentPath(dto.getProofDocumentPath());
        patent.setCreatedAt(LocalDateTime.now());
        patent.setUpdatedAt(LocalDateTime.now());
        
        patent = patentRepository.save(patent);
        return convertToDTO(patent);
    }
    
    public PatentDTO updatePatent(String id, String facultyId, PatentDTO dto) {
        Patent patent = patentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patent not found"));
        
        if (!patent.getFacultyId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to update this patent");
        }
        
        patent.setTitle(dto.getTitle());
        patent.setPatentNumber(dto.getPatentNumber());
        patent.setInventors(dto.getInventors());
        patent.setYear(dto.getYear());
        patent.setCountry(dto.getCountry());
        patent.setStatus(dto.getStatus());
        if (dto.getProofDocumentPath() != null) {
            patent.setProofDocumentPath(dto.getProofDocumentPath());
        }
        patent.setUpdatedAt(LocalDateTime.now());
        
        patent = patentRepository.save(patent);
        return convertToDTO(patent);
    }
    
    public List<PatentDTO> getPatentsByFaculty(String facultyId) {
        return patentRepository.findByFacultyId(facultyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PatentDTO> getAllPatents() {
        return patentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void deletePatent(String id, String facultyId) {
        Patent patent = patentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patent not found"));
        
        if (!patent.getFacultyId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to delete this patent");
        }
        
        patentRepository.delete(patent);
    }
    
    private PatentDTO convertToDTO(Patent patent) {
        PatentDTO dto = new PatentDTO();
        dto.setId(patent.getId());
        dto.setTitle(patent.getTitle());
        dto.setPatentNumber(patent.getPatentNumber());
        dto.setInventors(patent.getInventors());
        dto.setYear(patent.getYear());
        dto.setCountry(patent.getCountry());
        dto.setStatus(patent.getStatus());
        dto.setProofDocumentPath(patent.getProofDocumentPath());
        return dto;
    }
}


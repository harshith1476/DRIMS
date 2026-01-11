package com.drims.service;

import com.drims.dto.TargetDTO;
import com.drims.entity.Target;
import com.drims.repository.TargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TargetService {
    
    @Autowired
    private TargetRepository targetRepository;
    
    public TargetDTO createOrUpdateTarget(String facultyId, TargetDTO dto) {
        Target target = targetRepository.findByFacultyIdAndYear(facultyId, dto.getYear())
                .orElse(new Target());
        
        target.setFacultyId(facultyId);
        target.setYear(dto.getYear());
        target.setJournalTarget(dto.getJournalTarget());
        target.setConferenceTarget(dto.getConferenceTarget());
        target.setPatentTarget(dto.getPatentTarget());
        target.setBookChapterTarget(dto.getBookChapterTarget());
        
        if (target.getId() == null) {
            target.setCreatedAt(LocalDateTime.now());
        }
        target.setUpdatedAt(LocalDateTime.now());
        
        target = targetRepository.save(target);
        return convertToDTO(target);
    }
    
    public List<TargetDTO> getTargetsByFaculty(String facultyId) {
        return targetRepository.findByFacultyId(facultyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<TargetDTO> getAllTargets() {
        return targetRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private TargetDTO convertToDTO(Target target) {
        TargetDTO dto = new TargetDTO();
        dto.setId(target.getId());
        dto.setYear(target.getYear());
        dto.setJournalTarget(target.getJournalTarget());
        dto.setConferenceTarget(target.getConferenceTarget());
        dto.setPatentTarget(target.getPatentTarget());
        dto.setBookChapterTarget(target.getBookChapterTarget());
        return dto;
    }
}


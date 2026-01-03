package com.drims.service;

import com.drims.dto.FacultyProfileDTO;
import com.drims.entity.FacultyProfile;
import com.drims.entity.User;
import com.drims.repository.FacultyProfileRepository;
import com.drims.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacultyProfileService {
    
    @Autowired
    private FacultyProfileRepository facultyProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public FacultyProfileDTO getProfileByEmail(String email) {
        FacultyProfile profile = facultyProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return convertToDTO(profile);
    }
    
    public FacultyProfileDTO updateProfile(String email, FacultyProfileDTO dto) {
        FacultyProfile profile = facultyProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        profile.setName(dto.getName());
        profile.setDesignation(dto.getDesignation());
        profile.setDepartment(dto.getDepartment());
        profile.setResearchAreas(dto.getResearchAreas());
        profile.setOrcidId(dto.getOrcidId());
        profile.setScopusId(dto.getScopusId());
        profile.setGoogleScholarLink(dto.getGoogleScholarLink());
        profile.setUpdatedAt(LocalDateTime.now());
        
        profile = facultyProfileRepository.save(profile);
        return convertToDTO(profile);
    }
    
    public List<FacultyProfileDTO> getAllProfiles() {
        return facultyProfileRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public FacultyProfileDTO getProfileById(String id) {
        FacultyProfile profile = facultyProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return convertToDTO(profile);
    }
    
    private FacultyProfileDTO convertToDTO(FacultyProfile profile) {
        FacultyProfileDTO dto = new FacultyProfileDTO();
        dto.setId(profile.getId());
        dto.setEmployeeId(profile.getEmployeeId());
        dto.setName(profile.getName());
        dto.setDesignation(profile.getDesignation());
        dto.setDepartment(profile.getDepartment());
        dto.setResearchAreas(profile.getResearchAreas());
        dto.setOrcidId(profile.getOrcidId());
        dto.setScopusId(profile.getScopusId());
        dto.setGoogleScholarLink(profile.getGoogleScholarLink());
        dto.setEmail(profile.getEmail());
        return dto;
    }
}


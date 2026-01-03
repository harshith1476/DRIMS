package com.drims.service;

import com.drims.dto.ConferenceDTO;
import com.drims.entity.Conference;
import com.drims.repository.ConferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConferenceService {
    
    @Autowired
    private ConferenceRepository conferenceRepository;
    
    public ConferenceDTO createConference(String facultyId, ConferenceDTO dto) {
        Conference conference = new Conference();
        conference.setFacultyId(facultyId);
        conference.setTitle(dto.getTitle());
        conference.setConferenceName(dto.getConferenceName());
        conference.setAuthors(dto.getAuthors());
        conference.setYear(dto.getYear());
        conference.setLocation(dto.getLocation());
        conference.setDate(dto.getDate());
        conference.setStatus(dto.getStatus());
        conference.setProofDocumentPath(dto.getProofDocumentPath());
        conference.setCreatedAt(LocalDateTime.now());
        conference.setUpdatedAt(LocalDateTime.now());
        
        conference = conferenceRepository.save(conference);
        return convertToDTO(conference);
    }
    
    public ConferenceDTO updateConference(String id, String facultyId, ConferenceDTO dto) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        
        if (!conference.getFacultyId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to update this conference");
        }
        
        conference.setTitle(dto.getTitle());
        conference.setConferenceName(dto.getConferenceName());
        conference.setAuthors(dto.getAuthors());
        conference.setYear(dto.getYear());
        conference.setLocation(dto.getLocation());
        conference.setDate(dto.getDate());
        conference.setStatus(dto.getStatus());
        if (dto.getProofDocumentPath() != null) {
            conference.setProofDocumentPath(dto.getProofDocumentPath());
        }
        conference.setUpdatedAt(LocalDateTime.now());
        
        conference = conferenceRepository.save(conference);
        return convertToDTO(conference);
    }
    
    public List<ConferenceDTO> getConferencesByFaculty(String facultyId) {
        return conferenceRepository.findByFacultyId(facultyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ConferenceDTO> getAllConferences() {
        return conferenceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void deleteConference(String id, String facultyId) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found"));
        
        if (!conference.getFacultyId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to delete this conference");
        }
        
        conferenceRepository.delete(conference);
    }
    
    private ConferenceDTO convertToDTO(Conference conference) {
        ConferenceDTO dto = new ConferenceDTO();
        dto.setId(conference.getId());
        dto.setTitle(conference.getTitle());
        dto.setConferenceName(conference.getConferenceName());
        dto.setAuthors(conference.getAuthors());
        dto.setYear(conference.getYear());
        dto.setLocation(conference.getLocation());
        dto.setDate(conference.getDate());
        dto.setStatus(conference.getStatus());
        dto.setProofDocumentPath(conference.getProofDocumentPath());
        return dto;
    }
}


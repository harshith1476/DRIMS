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
        conference.setOrganizer(dto.getOrganizer());
        conference.setAuthors(dto.getAuthors());
        conference.setYear(dto.getYear());
        conference.setLocation(dto.getLocation());
        conference.setDate(dto.getDate());
        conference.setStatus(dto.getStatus());
        conference.setCategory(dto.getCategory());
        conference.setRegistrationAmount(dto.getRegistrationAmount());
        conference.setPaymentMode(dto.getPaymentMode());
        conference.setIsStudentPublication(dto.getStudentName() != null);
        conference.setStudentName(dto.getStudentName());
        conference.setStudentRegisterNumber(dto.getStudentRegisterNumber());
        conference.setGuideId(dto.getGuideId());
        conference.setGuideName(dto.getGuideName());
        conference.setApprovalStatus("SUBMITTED");
        conference.setRegistrationReceiptPath(dto.getRegistrationReceiptPath());
        conference.setCertificatePath(dto.getCertificatePath());
        conference.setProofDocumentPath(dto.getProofDocumentPath()); // Legacy
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
        
        // Don't allow updates if already approved/locked
        if ("APPROVED".equals(conference.getApprovalStatus()) || "LOCKED".equals(conference.getApprovalStatus())) {
            throw new RuntimeException("Cannot update approved/locked conference");
        }
        
        conference.setTitle(dto.getTitle());
        conference.setConferenceName(dto.getConferenceName());
        conference.setOrganizer(dto.getOrganizer());
        conference.setAuthors(dto.getAuthors());
        conference.setYear(dto.getYear());
        conference.setLocation(dto.getLocation());
        conference.setDate(dto.getDate());
        conference.setStatus(dto.getStatus());
        conference.setCategory(dto.getCategory());
        conference.setRegistrationAmount(dto.getRegistrationAmount());
        conference.setPaymentMode(dto.getPaymentMode());
        if (dto.getRegistrationReceiptPath() != null) {
            conference.setRegistrationReceiptPath(dto.getRegistrationReceiptPath());
        }
        if (dto.getCertificatePath() != null) {
            conference.setCertificatePath(dto.getCertificatePath());
        }
        if (dto.getProofDocumentPath() != null) {
            conference.setProofDocumentPath(dto.getProofDocumentPath()); // Legacy
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
        
        // Don't allow deletion if already approved/locked
        if ("APPROVED".equals(conference.getApprovalStatus()) || "LOCKED".equals(conference.getApprovalStatus())) {
            throw new RuntimeException("Cannot delete approved/locked conference");
        }
        
        conferenceRepository.delete(conference);
    }
    
    private ConferenceDTO convertToDTO(Conference conference) {
        ConferenceDTO dto = new ConferenceDTO();
        dto.setId(conference.getId());
        dto.setTitle(conference.getTitle());
        dto.setConferenceName(conference.getConferenceName());
        dto.setOrganizer(conference.getOrganizer());
        dto.setAuthors(conference.getAuthors());
        dto.setYear(conference.getYear());
        dto.setLocation(conference.getLocation());
        dto.setDate(conference.getDate());
        dto.setStatus(conference.getStatus());
        dto.setCategory(conference.getCategory());
        dto.setRegistrationAmount(conference.getRegistrationAmount());
        dto.setPaymentMode(conference.getPaymentMode());
        dto.setStudentName(conference.getStudentName());
        dto.setStudentRegisterNumber(conference.getStudentRegisterNumber());
        dto.setGuideId(conference.getGuideId());
        dto.setGuideName(conference.getGuideName());
        dto.setApprovalStatus(conference.getApprovalStatus());
        dto.setRemarks(conference.getRemarks());
        dto.setRegistrationReceiptPath(conference.getRegistrationReceiptPath());
        dto.setCertificatePath(conference.getCertificatePath());
        dto.setProofDocumentPath(conference.getProofDocumentPath()); // Legacy
        return dto;
    }
}


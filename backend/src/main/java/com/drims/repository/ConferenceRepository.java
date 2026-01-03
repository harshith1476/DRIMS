package com.drims.repository;

import com.drims.entity.Conference;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConferenceRepository extends MongoRepository<Conference, String> {
    List<Conference> findByFacultyId(String facultyId);
    List<Conference> findAll();
    List<Conference> findByYear(Integer year);
    List<Conference> findByFacultyIdAndYear(String facultyId, Integer year);
}


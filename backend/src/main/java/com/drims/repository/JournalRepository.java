package com.drims.repository;

import com.drims.entity.Journal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalRepository extends MongoRepository<Journal, String> {
    List<Journal> findByFacultyId(String facultyId);
    List<Journal> findAll();
    List<Journal> findByYear(Integer year);
    List<Journal> findByFacultyIdAndYear(String facultyId, Integer year);
}


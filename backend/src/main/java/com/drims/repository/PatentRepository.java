package com.drims.repository;

import com.drims.entity.Patent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatentRepository extends MongoRepository<Patent, String> {
    List<Patent> findByFacultyId(String facultyId);
    List<Patent> findAll();
    List<Patent> findByYear(Integer year);
    List<Patent> findByFacultyIdAndYear(String facultyId, Integer year);
}


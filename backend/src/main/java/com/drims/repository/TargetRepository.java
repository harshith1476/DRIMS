package com.drims.repository;

import com.drims.entity.Target;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TargetRepository extends MongoRepository<Target, String> {
    List<Target> findByFacultyId(String facultyId);
    Optional<Target> findByFacultyIdAndYear(String facultyId, Integer year);
    List<Target> findAll();
}


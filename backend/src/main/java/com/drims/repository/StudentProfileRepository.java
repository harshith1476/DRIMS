package com.drims.repository;

import com.drims.entity.StudentProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends MongoRepository<StudentProfile, String> {
    Optional<StudentProfile> findByRegisterNumber(String registerNumber);
    Optional<StudentProfile> findByUserId(String userId);
    boolean existsByRegisterNumber(String registerNumber);
}

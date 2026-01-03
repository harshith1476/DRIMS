package com.drims.repository;

import com.drims.entity.BookChapter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookChapterRepository extends MongoRepository<BookChapter, String> {
    List<BookChapter> findByFacultyId(String facultyId);
    List<BookChapter> findAll();
    List<BookChapter> findByYear(Integer year);
    List<BookChapter> findByFacultyIdAndYear(String facultyId, Integer year);
}


package com.drims.repository;

import com.drims.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findByFacultyId(String facultyId);
    List<Book> findByApprovalStatus(String approvalStatus);
    List<Book> findByApprovalStatusIn(List<String> approvalStatuses);
    List<Book> findByPublicationYear(Integer publicationYear);
    List<Book> findByFacultyIdAndPublicationYear(String facultyId, Integer publicationYear);
}

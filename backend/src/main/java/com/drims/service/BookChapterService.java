package com.drims.service;

import com.drims.dto.BookChapterDTO;
import com.drims.entity.BookChapter;
import com.drims.repository.BookChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookChapterService {
    
    @Autowired
    private BookChapterRepository bookChapterRepository;
    
    public BookChapterDTO createBookChapter(String facultyId, BookChapterDTO dto) {
        BookChapter bookChapter = new BookChapter();
        bookChapter.setFacultyId(facultyId);
        bookChapter.setTitle(dto.getTitle());
        bookChapter.setBookTitle(dto.getBookTitle());
        bookChapter.setAuthors(dto.getAuthors());
        bookChapter.setEditors(dto.getEditors());
        bookChapter.setPublisher(dto.getPublisher());
        bookChapter.setYear(dto.getYear());
        bookChapter.setPages(dto.getPages());
        bookChapter.setIsbn(dto.getIsbn());
        bookChapter.setStatus(dto.getStatus());
        bookChapter.setProofDocumentPath(dto.getProofDocumentPath());
        bookChapter.setCreatedAt(LocalDateTime.now());
        bookChapter.setUpdatedAt(LocalDateTime.now());
        
        bookChapter = bookChapterRepository.save(bookChapter);
        return convertToDTO(bookChapter);
    }
    
    public BookChapterDTO updateBookChapter(String id, String facultyId, BookChapterDTO dto) {
        BookChapter bookChapter = bookChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book chapter not found"));
        
        if (!bookChapter.getFacultyId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to update this book chapter");
        }
        
        bookChapter.setTitle(dto.getTitle());
        bookChapter.setBookTitle(dto.getBookTitle());
        bookChapter.setAuthors(dto.getAuthors());
        bookChapter.setEditors(dto.getEditors());
        bookChapter.setPublisher(dto.getPublisher());
        bookChapter.setYear(dto.getYear());
        bookChapter.setPages(dto.getPages());
        bookChapter.setIsbn(dto.getIsbn());
        bookChapter.setStatus(dto.getStatus());
        if (dto.getProofDocumentPath() != null) {
            bookChapter.setProofDocumentPath(dto.getProofDocumentPath());
        }
        bookChapter.setUpdatedAt(LocalDateTime.now());
        
        bookChapter = bookChapterRepository.save(bookChapter);
        return convertToDTO(bookChapter);
    }
    
    public List<BookChapterDTO> getBookChaptersByFaculty(String facultyId) {
        return bookChapterRepository.findByFacultyId(facultyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookChapterDTO> getAllBookChapters() {
        return bookChapterRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void deleteBookChapter(String id, String facultyId) {
        BookChapter bookChapter = bookChapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book chapter not found"));
        
        if (!bookChapter.getFacultyId().equals(facultyId)) {
            throw new RuntimeException("Unauthorized to delete this book chapter");
        }
        
        bookChapterRepository.delete(bookChapter);
    }
    
    private BookChapterDTO convertToDTO(BookChapter bookChapter) {
        BookChapterDTO dto = new BookChapterDTO();
        dto.setId(bookChapter.getId());
        dto.setTitle(bookChapter.getTitle());
        dto.setBookTitle(bookChapter.getBookTitle());
        dto.setAuthors(bookChapter.getAuthors());
        dto.setEditors(bookChapter.getEditors());
        dto.setPublisher(bookChapter.getPublisher());
        dto.setYear(bookChapter.getYear());
        dto.setPages(bookChapter.getPages());
        dto.setIsbn(bookChapter.getIsbn());
        dto.setStatus(bookChapter.getStatus());
        dto.setProofDocumentPath(bookChapter.getProofDocumentPath());
        return dto;
    }
}


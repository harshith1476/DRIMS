package com.drims.config;

import com.drims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Utility class to clear database if needed
 * Run with: java -jar app.jar --spring.main.web-application-type=none --spring.profiles.active=clear
 */
@Component
public class ClearDatabase implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FacultyProfileRepository facultyProfileRepository;
    
    @Autowired
    private JournalRepository journalRepository;
    
    @Autowired
    private ConferenceRepository conferenceRepository;
    
    @Autowired
    private PatentRepository patentRepository;
    
    @Autowired
    private BookChapterRepository bookChapterRepository;
    
    @Autowired
    private TargetRepository targetRepository;
    
    @Override
    public void run(String... args) {
        // Only clear if explicitly requested
        boolean shouldClear = false;
        for (String arg : args) {
            if (arg.equals("--clear-db")) {
                shouldClear = true;
                break;
            }
        }
        
        if (shouldClear) {
            System.out.println("Clearing database...");
            journalRepository.deleteAll();
            conferenceRepository.deleteAll();
            patentRepository.deleteAll();
            bookChapterRepository.deleteAll();
            targetRepository.deleteAll();
            facultyProfileRepository.deleteAll();
            userRepository.deleteAll();
            System.out.println("Database cleared!");
        }
    }
}


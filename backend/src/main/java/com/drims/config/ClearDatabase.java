package com.drims.config;

import com.drims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class to clear database if needed
 * Run with: java -jar app.jar --clear-db
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
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public void run(String... args) {
        // Only clear if explicitly requested OR if --clear-db flag is present
        boolean shouldClear = false;
        for (String arg : args) {
            if (arg.equals("--clear-db") || arg.equals("clear-db")) {
                shouldClear = true;
                break;
            }
        }
        
        // Also check environment variable or system property
        String clearDbProperty = System.getProperty("clear.db");
        if (clearDbProperty != null && clearDbProperty.equalsIgnoreCase("true")) {
            shouldClear = true;
        }
        
        if (shouldClear) {
            System.out.println("========================================");
            System.out.println("Clearing database completely...");
            System.out.println("This will drop all collections and indexes!");
            System.out.println("========================================");
            
            try {
                // Drop collections completely (this also drops all indexes)
                mongoTemplate.dropCollection("users");
                mongoTemplate.dropCollection("faculty_profiles");
                mongoTemplate.dropCollection("journals");
                mongoTemplate.dropCollection("conferences");
                mongoTemplate.dropCollection("patents");
                mongoTemplate.dropCollection("book_chapters");
                mongoTemplate.dropCollection("targets");
                System.out.println("All collections dropped successfully!");
                System.out.println("All indexes removed!");
            } catch (Exception e) {
                System.out.println("Error dropping collections, trying deleteAll instead: " + e.getMessage());
                // Fallback to deleteAll if dropCollection fails
                journalRepository.deleteAll();
                conferenceRepository.deleteAll();
                patentRepository.deleteAll();
                bookChapterRepository.deleteAll();
                targetRepository.deleteAll();
                facultyProfileRepository.deleteAll();
                userRepository.deleteAll();
                System.out.println("All data deleted (indexes may still exist - restart MongoDB to fully clear)");
            }
            
            System.out.println("========================================");
            System.out.println("Database cleared successfully!");
            System.out.println("You can now restart the backend to load all 74 faculty.");
            System.out.println("========================================");
        }
    }
}


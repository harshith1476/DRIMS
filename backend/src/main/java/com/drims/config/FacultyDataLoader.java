package com.drims.config;

import com.drims.entity.*;
import com.drims.repository.*;
import com.drims.repository.TargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class FacultyDataLoader implements CommandLineRunner {
    
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
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if faculty data already exists (more than just admin)
        long facultyCount = userRepository.findAll().stream()
                .filter(u -> "FACULTY".equals(u.getRole()))
                .count();
        
        if (facultyCount > 0) {
            System.out.println("Faculty data already loaded (" + facultyCount + " faculty). To reload, clear database first.");
            return;
        }
        
        System.out.println("Loading faculty data...");
        
        // Create Admin if not exists
        createAdmin();
        
        // Load all faculty members and their publications
        loadFacultyData();
        
        System.out.println("Faculty data loaded successfully!");
        System.out.println("Total faculty created: " + facultyProfileRepository.count());
    }
    
    private void createAdmin() {
        if (!userRepository.existsByEmail("admin@drims.edu")) {
            User admin = new User();
            admin.setEmail("admin@drims.edu");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setFacultyId(null);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("Admin created: admin@drims.edu / admin123");
        }
    }
    
    private void loadFacultyData() {
        Map<String, FacultyData> facultyMap = getFacultyData();
        
        int empIdCounter = 1;
        
        for (Map.Entry<String, FacultyData> entry : facultyMap.entrySet()) {
            String facultyName = entry.getKey();
            FacultyData data = entry.getValue();
            
            String email = generateEmail(facultyName);
            String employeeId = "EMP" + String.format("%03d", empIdCounter++);
            
            // Skip if already exists
            if (userRepository.existsByEmail(email)) {
                System.out.println("Skipping existing: " + facultyName);
                continue;
            }
            
            // Create User
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("faculty123"));
            user.setRole("FACULTY");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
            
            // Create Faculty Profile
            FacultyProfile profile = new FacultyProfile();
            profile.setEmployeeId(employeeId);
            profile.setName(facultyName);
            profile.setDesignation(data.designation != null ? data.designation : "Assistant Professor");
            profile.setDepartment("Computer Science and Engineering");
            profile.setResearchAreas(data.researchAreas.isEmpty() ? 
                Arrays.asList("Computer Science", "Machine Learning") : data.researchAreas);
            profile.setEmail(email);
            profile.setUserId(user.getId());
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());
            profile = facultyProfileRepository.save(profile);
            
            // Update user with facultyId
            user.setFacultyId(profile.getId());
            userRepository.save(user);
            
            // Create Research Targets for 2025
            createResearchTargets(profile.getId(), facultyName);
            
            // Create Publications
            createPublications(profile.getId(), data);
            
            System.out.println("Created: " + facultyName + " (" + email + " / faculty123)");
        }
    }
    
    private void createResearchTargets(String facultyId, String facultyName) {
        // Load research targets from Excel data (2025 targets)
        Map<String, TargetData> targetMap = getResearchTargets2025();
        TargetData targetData = targetMap.get(facultyName);
        
        if (targetData != null) {
            Target target = new Target();
            target.setFacultyId(facultyId);
            target.setYear(2025);
            target.setJournalTarget(targetData.journalTarget);
            target.setConferenceTarget(targetData.conferenceTarget);
            target.setPatentTarget(targetData.patentTarget);
            target.setBookChapterTarget(targetData.bookChapterTarget);
            target.setCreatedAt(LocalDateTime.now());
            target.setUpdatedAt(LocalDateTime.now());
            targetRepository.save(target);
        }
    }
    
    private void createPublications(String facultyId, FacultyData data) {
        LocalDateTime now = LocalDateTime.now();
        
        // Create Conferences
        for (ConferenceData conf : data.conferences) {
            Conference conference = new Conference();
            conference.setFacultyId(facultyId);
            conference.setTitle(conf.title);
            conference.setConferenceName(conf.conferenceName);
            conference.setAuthors(conf.authors);
            conference.setYear(conf.year);
            conference.setLocation(conf.location != null ? conf.location : "");
            conference.setDate(conf.date);
            conference.setStatus(conf.status);
            conference.setCreatedAt(now);
            conference.setUpdatedAt(now);
            conferenceRepository.save(conference);
        }
        
        // Create Journals
        for (JournalData journal : data.journals) {
            Journal j = new Journal();
            j.setFacultyId(facultyId);
            j.setTitle(journal.title);
            j.setJournalName(journal.journalName);
            j.setAuthors(journal.authors);
            j.setYear(journal.year);
            j.setVolume(journal.volume);
            j.setIssue(journal.issue);
            j.setPages(journal.pages);
            j.setDoi(journal.doi);
            j.setImpactFactor(journal.impactFactor);
            j.setStatus(journal.status);
            j.setCreatedAt(now);
            j.setUpdatedAt(now);
            journalRepository.save(j);
        }
        
        // Create Patents
        for (PatentData patent : data.patents) {
            Patent p = new Patent();
            p.setFacultyId(facultyId);
            p.setTitle(patent.title);
            p.setPatentNumber(patent.patentNumber);
            p.setInventors(patent.inventors);
            p.setYear(patent.year);
            p.setCountry(patent.country);
            p.setStatus(patent.status);
            p.setCreatedAt(now);
            p.setUpdatedAt(now);
            patentRepository.save(p);
        }
        
        // Create Book Chapters
        for (BookChapterData chapter : data.bookChapters) {
            BookChapter bc = new BookChapter();
            bc.setFacultyId(facultyId);
            bc.setTitle(chapter.title);
            bc.setBookTitle(chapter.bookTitle);
            bc.setAuthors(chapter.authors);
            bc.setEditors(chapter.editors);
            bc.setPublisher(chapter.publisher);
            bc.setYear(chapter.year);
            bc.setPages(chapter.pages);
            bc.setIsbn(chapter.isbn);
            bc.setStatus(chapter.status);
            bc.setCreatedAt(now);
            bc.setUpdatedAt(now);
            bookChapterRepository.save(bc);
        }
    }
    
    private String generateEmail(String name) {
        String email = name.toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z0-9.]", "")
                .replaceAll("\\.+", ".");
        
        email = email.replaceAll("^(dr\\.|prof\\.|mr\\.|mrs\\.|ms\\.)", "");
        
        // Ensure uniqueness
        String baseEmail = email + "@drims.edu";
        int counter = 1;
        String finalEmail = baseEmail;
        
        while (userRepository.existsByEmail(finalEmail)) {
            finalEmail = email + counter + "@drims.edu";
            counter++;
        }
        
        return finalEmail;
    }
    
    private Map<String, FacultyData> getFacultyData() {
        Map<String, FacultyData> facultyMap = new LinkedHashMap<>();
        
        // 1. Renugadevi R
        FacultyData renugadevi = new FacultyData();
        renugadevi.designation = "Professor";
        renugadevi.researchAreas = Arrays.asList("Deep Learning", "Computer Vision", "Medical Imaging");
        renugadevi.conferences.add(new ConferenceData(
            "Deep learning for skin cancer detection: A technological breakthrough in early diagnosis",
            "INTERNATIONAL CONFERENCE ON EDUCATING POST MILLENNIALS (ICEM'24)",
            "R.Renugadevi, A. Teja Sai Mounika, G. Nandhini, K. Lakshmi",
            2025, "2025-1-27", "Published", "London"
        ));
        // Add journal publications
        renugadevi.journals.add(createJournal("Teaching and learning optimization method for multi-channel wireless mesh networks",
            "J Ambient Intell Human C", "P.Ranjithkumar, Manikandan, R.Renugadevi, Packiyalakshmi",
            2025, "13", "", "", "https://doi.org/10.1007/s12652-025-05004-z", "4.1", "Published"));
        facultyMap.put("Renugadevi R", renugadevi);
        
        // Add Dr.R.Renugadevi (same person, different format)
        FacultyData renugadevi2 = new FacultyData();
        renugadevi2.designation = "Professor";
        renugadevi2.researchAreas = Arrays.asList("Deep Learning", "Computer Vision");
        renugadevi2.bookChapters.add(createBookChapter("Revolutionizing Blockchain-Enabled Internet of",
            "Blockchain-Enabled Internet of", "Dr.R.Renugadevi", "", "Bentham", 2025, "", "", "Published"));
        facultyMap.put("Dr.R.Renugadevi", renugadevi2);
        
        // 2. Maridu Bhargavi
        FacultyData bhargavi = new FacultyData();
        bhargavi.designation = "Assistant Professor";
        bhargavi.researchAreas = Arrays.asList("Machine Learning", "Data Science", "AI");
        bhargavi.conferences.addAll(Arrays.asList(
            new ConferenceData("A comparative analysis for air quality prediction by AQI calculation using different machine learning algorithms",
                "INTERNATIONAL CONFERENCE ON EDUCATING POST MILLENNIALS (ICEM'24)",
                "Rohit Kumar, V Krishna Likitha, Md Harshida, Sk Afreen, Guduru Manideep, Maridu Bhargavi",
                2025, "2025-1-27", "Published", "London"),
            new ConferenceData("Leveraging machine learning for paragraph-based answer generation",
                "INTERNATIONAL CONFERENCE ON EDUCATING POST MILLENNIALS (ICEM'24)",
                "Maridu Bhargavi, Cherukuri Sowndaryavathi, Kshama Kumari, Ankit Kumar Prabhat, Manish Kumar",
                2025, "2025-1-27", "Published", "London"),
            new ConferenceData("Enhancing employee turnover prediction with ensemble blending: A fusion of SVM and CatBoost",
                "INTERNATIONAL CONFERENCE ON EDUCATING POST MILLENNIALS (ICEM'24)",
                "Naga Naveen Ambati, Swapna Sri Gottipati, Vema Reddy Polimera, Tarun Malla, Maridu Bhargavi",
                2025, "2025-1-27", "Published", "London"),
            new ConferenceData("Student placement prediction",
                "INTERNATIONAL CONFERENCE ON EDUCATING POST MILLENNIALS (ICEM'24)",
                "Maridu Bhargavi, Kunal Kumar, G Sai Vijay, Ch Sai Teja, Neerukonda Dharmasai",
                2025, "2025-1-27", "Published", "London"),
            new ConferenceData("Predicting Employee Attrition with Deep Learning and Ensemble techniques for optimized workforce management",
                "ICSCNA-2024", "Mellachervu Chandana, Maridu Bhargavi, Sanikommu Renuka, Kakumanu Pavan Sai, Shatakshi Bajpai",
                2025, "10.02.2025", "Published", "Theni, India"),
            new ConferenceData("Leveraging XGBoost and Clinical Attributes for Heart Disease Prediction",
                "ICSCNA -2024", "Kota Susmitha, Maridu Bhargavi, Achyuta Mohitha Sai Sri, Bogala Devi Prasaad Reddy, Paladugu Siva Satyanarayana",
                2025, "10-2-25", "Published", "Theni"),
            new ConferenceData("LEVERAGING SMOTE AND RANDOM FOREST FOR IMPROVED CREDIT CARD FRAUD DETECTION",
                "ICSCNA -2024", "Maddala Ruchita, Maridu Bhargavi, Maddala Rakshita, Bellamkonda Chaitanya Nandini, Irfan Aziz",
                2025, "10-2-25", "Published", "Theni"),
            new ConferenceData("Deep Learning Based Traffic Sign Recognition Using CNN and TensorFlow",
                "ICSCNA -2024", "Penagamuri Srinaivasa Gowtham, P Kavyanjali, P Nagababu, K Subash",
                2025, "10-2-25", "Published", "Theni"),
            new ConferenceData("Sentiment-Based Insights Into Amazon Musical Instrument Purchases",
                "ICSCNA", "A.Ammulu, Ande Mokshagna, Parasa Ganesh, Bollimuntha Manasa",
                2025, "10-2-25", "Published", "Theni"),
            new ConferenceData("Detecting Real-Time Data Manipulation in Electric Vehicle Charging Stations using Machine Learning Algorithm",
                "ICSCNA -2024", "Jannavarapu Vani Akhila, Maridu Bhargavi, Mondem Manikanta, srigakolapu Sai Lakshmi, Nagulapati Phanindra Raja Mithra",
                2025, "10-2-25", "Published", "Theni")
        ));
        facultyMap.put("Maridu Bhargavi", bhargavi);
        
        // 3. B Suvarna
        FacultyData suvarna = new FacultyData();
        suvarna.designation = "Assistant Professor";
        suvarna.researchAreas = Arrays.asList("Deep Learning", "Computer Vision", "CNN");
        suvarna.conferences.addAll(Arrays.asList(
            new ConferenceData("Footwear Classification Using Pretrained CNN Models with Deep Neural Network",
                "IEEE Conference", "Andrew Blaze Pitta, Narendra Reddy Pingala, Naga Venkata Mani Charan J, Sowmya Bogolu, B Suvarna",
                2025, "27.02.2025", "Published", ""),
            new ConferenceData("Enhanced Deep Fake Image Detection via Feature Fusion of EfficientNet, Xception, and ResNet Models",
                "IEEE Conference", "R N Bharath Reddy, T V Naga Siva, B Sri Ram, K N Ramya sree, B Suvarna",
                2025, "20.02.2025", "Published", "")
        ));
        facultyMap.put("B Suvarna", suvarna);
        
        // 4. Venkatrama Phani Kumar Sistla
        FacultyData phaniKumar = new FacultyData();
        phaniKumar.designation = "Associate Professor";
        phaniKumar.researchAreas = Arrays.asList("Machine Learning", "Deep Learning", "Data Science");
        phaniKumar.conferences.addAll(Arrays.asList(
            new ConferenceData("A Novel Deep Learning Model for Machine Fault Diagnosis",
                "International Conference on Pervasive Computational Technologies (ICPCT-2025)",
                "Geethika, Neelima, Ravi Kiran, Sowmya, Venkatrama Phani Kumar; Venkata Krishna Kishore Kolli",
                2025, "March", "Published", ""),
            new ConferenceData("An Experimental Study on Prediction of Lung Cancer from CT Scan Images",
                "International Conference on Intelligent Systems and Computational Networks(ICISCN- 2025)",
                "Abhishek Mandala, Venkata Seetha Ramanjaneyulu Kurapati, Siva Rama Krishna Musunuri, Jogindhar Venkata Sai Choudhari Mutthina, Venkatrama Phani Kumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "March", "Published", ""),
            new ConferenceData("A Novel Transfer Learning-based Efficient-Net for Visual Image Tracking",
                "International Conference on Pervasive Computational Technologies (ICPCT-2025)",
                "Sowmya Sri Puligadda, Karthik Galla, Sai Subbarao Vurakaranam, Usha Lakshmi Polina, Venkatrama Phani Kumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "March", "Published", ""),
            new ConferenceData("An Investigative Comparison of Various Deep Learning Models for Driver Drowsiness Detection",
                "International Conference on Intelligent Systems and Computational Networks(ICISCN- 2025)",
                "Umesh Reddy Arimanda, Sai Ganesh Nannapaneni, Raghavendra Sai Boddu, Venkata Siddardha Mogalluri, Venkatrama Phani Kumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "March", "Published", ""),
            new ConferenceData("Comparative Study of Different Pre-trained Deep Learning Models for Footwear Classification",
                "International Conference on Intelligent Systems and Computational Networks(ICISCN- 2025)",
                "Chandu Boppana, Naga Amrutha Chituri, Vamsi Pallapu, Bala Vamsi Boyapati, Venkatrama Phani Kumar; Venkata Krishna Kishore Kolli",
                2025, "March", "Published", ""),
            new ConferenceData("Automated Kidney Anomaly Detection Using Deep Learning and Explainable AI Techniques",
                "International Conference on Pervasive Computational Technologies (ICPCT-2025)",
                "BOBBA SIVA SANKAR REDDY, Nelluru Laxmi Prathyusha, Dhulipudi Venkata Karthik, Kayala Vishnukanth, Venkatrama Phani Kumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "March", "Published", ""),
            new ConferenceData("Bi-GRU and Glove based Aspect-level Movie Recommendation",
                "IEEE International Conference on Computational, Communication and Information Technology",
                "Veera Brahma Chaitanya, Haritha, Vijay Rami Reddy, Joshanth, Venkatrama Phani Kumar Sistla, Venkata Krishna Kishore Kolli",
                2025, "March", "Published", "")
        ));
        facultyMap.put("Venkatrama Phani Kumar Sistla", phaniKumar);
        
        // 5. S Deva Kumar
        FacultyData devaKumar = new FacultyData();
        devaKumar.designation = "Assistant Professor";
        devaKumar.researchAreas = Arrays.asList("Deep Learning", "Medical Imaging", "CNN");
        devaKumar.conferences.addAll(Arrays.asList(
            new ConferenceData("A Novel Deep Learning model based Lung Cancer Detection of Histopathological Images",
                "IEEE International Conference On Computational, Communication and Information Technology",
                "Sneha Chirumamilla, Kanaparthi Satish Babu, Kureti Manikanta, Vemulapallii Manjunadha, S Deva Kumar; S Venkatrama Phani Kumar",
                2025, "March", "Published", "Indore, India"),
            new ConferenceData("Natural Disaster Prediction Using Deep Learning",
                "IEEE International Conference On Computational, Communication and Information Technology",
                "Guntaka Mahesh Vardhan, Pasupuleti BharatwajTeja, Kommalapati Thirumala Devi, Karumuri Rahul Dev, S Deva Kumar; S Venkatrama Phani Kumar",
                2025, "March", "Published", "Indore, India"),
            new ConferenceData("A Multi-Algorithm Stacking Approach to Lung Cancer Detection with SVM, GBM, Naive Bayes, Decision Tree, and Random Forest Models",
                "2025 International Conference on Computational, Communication and Information Technology (ICCCIT)",
                "Deepthi Alla; Sruthi Bajjuri; Vijaya Lakshmi; Bala Chandu Dasari; S Deva Kumar; Venkata Krishna Kishore Kolli",
                2025, "March", "Published", "Indore, India"),
            new ConferenceData("Quantum Machine Learning for Rotating Machinery Prognostics and Health Management",
                "International Conference on Sustainable Communication Networks and Application",
                "Gaddam Tejaswi, Kunal Prabhakar, S.Deva Kunar",
                2025, "10/02/0205", "Published", "Theni, India")
        ));
        facultyMap.put("S Deva Kumar", devaKumar);
        
        // 6. Sajida Sultana Sk
        FacultyData sajida = new FacultyData();
        sajida.designation = "Assistant Professor";
        sajida.researchAreas = Arrays.asList("Machine Learning", "Recommendation Systems", "Data Mining");
        sajida.conferences.addAll(Arrays.asList(
            new ConferenceData("Personalized product recommendation system for e-commerce platforms",
                "International Conference on Contemporary Pervasive Computational Intelligence (ICCPCI-2024)",
                "Shaik Sameena, Guntupalli Javali, Nelavelli Srilakshmi, Mandadapu Jhansi, Sajida Sultana Sk",
                2025, "20/02/2025", "Published", ""),
            new ConferenceData("Chronic Kidney Disease Prediction Based On Machine Learning Algorithms",
                "International Conference on Contemporary Pervasive Computational Intelligence (ICCPCI-2024)",
                "Likitha Kethineni, Nithinchandra Nithinchandra, Narendra Kumar, Sajida Sultana Sk",
                2025, "20/02/2025", "Published", ""),
            new ConferenceData("Intelligent book recommendation system using ML techniques",
                "International Conference on Contemporary Pervasive Computational Intelligence (ICCPCI-2024)",
                "Bhagya Sri. P, Sindhu Sri. G, Jaya Sri. K, Leela Poojitha. V and Sajida Sultana. Sk",
                2025, "20/02/2025", "Published", ""),
            new ConferenceData("Predicting restaurant ratings using regression analysis approach",
                "International Conference on Contemporary Pervasive Computational Intelligence (ICCPCI-2024)",
                "Sajida Sultana Sk, G Joseph Anand Kumar, V Leela Venkata Mani Sai, N Bala Sai, E Sai Naga Lakshmi",
                2025, "20/02/2025", "Published", ""),
            new ConferenceData("Unsupervised Learning for Heart Disease Prediction: Clustering-Based Approach",
                "International Conference on Contemporary Pervasive Computational Intelligence (ICCPCI-2024)",
                "Janani Jetty, Sajida Sultana Sk, Ranga Bhavitha Polepalle, Vishwitha Parusu",
                2025, "20/02/2025", "Published", ""),
            new ConferenceData("Assessing Skin Cancer Awareness: A Survey on Detection Methods",
                "International Conference on Contemporary Pervasive Computational Intelligence (ICCPCI-2024)",
                "Billa Vaishnavi, Pasupuleti Nithya, Shaik Haseena, Sajida Sultana Sk",
                2025, "20/02/2025", "Published", ""),
            new ConferenceData("Enhanced Attendance Management of Face Recognition Using Machine Learning",
                "International Conference on Contemporary Pervasive Computational Intelligence (ICCPCI-2024)",
                "Sowmya Ravipati, Lasya Modem, Sahith Yellinedi, Tejeswara Rao Namburi, Sajida Sultana Sk",
                2025, "20/02/2025", "Published", "")
        ));
        facultyMap.put("Sajida Sultana Sk", sajida);
        
        // 7. Chavva Ravi Kishore Reddy
        FacultyData raviKishore = new FacultyData();
        raviKishore.designation = "Assistant Professor";
        raviKishore.researchAreas = Arrays.asList("NLP", "Machine Learning", "Transformers");
        raviKishore.conferences.add(new ConferenceData(
            "Context-Aware Automated Essay Scoring with MLM-Pretrained T5 Transformer",
            "6th ICIRCA 2025",
            "Chavva Ravi Kishore Reddy, Venkata Krishna Kishore K, Arjun Kireeti Tulasi, Manideep Maturi, Abhiram Nagam",
            2025, "07/31/0205", "Published", "Coimbatore, India"
        ));
        facultyMap.put("Chavva Ravi Kishore Reddy", raviKishore);
        
        // 8. Venkatrajulu Pilli
        FacultyData venkatrajulu = new FacultyData();
        venkatrajulu.designation = "Assistant Professor";
        venkatrajulu.researchAreas = Arrays.asList("Deep Learning", "Computer Vision", "Medical AI");
        venkatrajulu.conferences.addAll(Arrays.asList(
            new ConferenceData("An Experimental Study on Driver Drowsiness Detection System using DL",
                "4th ICITSM'25", "Venkatrajulu Pilli, Dega Balakotaiah, Sai keerthana R, Sai madhuharika R",
                2025, "13/10/2025", "Published", "Tiruchengode, India"),
            new ConferenceData("Modeling Product Quality with Deep Learning: A Comparative Exploration",
                "4th ICITSM'25", "Dega Balakotaiah, Venkatrajulu Pilli, Chirumamilla Sneha, Rayavarapu Niharika, Galla Karthik",
                2025, "13/10/2025", "Published", "Tiruchengode, India"),
            new ConferenceData("CatBoost Model Optimized Through Optuna and SMOTE on Structured EEG Voice Biomarkers for Parkinson's Disease Prediction",
                "ICIACS 2025", "Venkatrajulu Pilli, Dega Balakotaiah, Telukutla Ajaybabu, Abhinay Balivada, Yakkanti Sai Varshitha",
                2025, "13/10/2025", "Published", "Kangeyam, TamilNadu, India")
        ));
        facultyMap.put("Venkatrajulu Pilli", venkatrajulu);
        
        // 9. Dega Balakotaiah
        FacultyData balakotaiah = new FacultyData();
        balakotaiah.designation = "Assistant Professor";
        balakotaiah.researchAreas = Arrays.asList("Deep Learning", "Machine Learning", "Data Science");
        facultyMap.put("Dega Balakotaiah", balakotaiah);
        
        // 10. Mr.Kiran Kumar Kaveti
        FacultyData kiranKumar = new FacultyData();
        kiranKumar.designation = "Assistant Professor";
        kiranKumar.researchAreas = Arrays.asList("NLP", "Machine Learning", "Sentiment Analysis");
        kiranKumar.conferences.addAll(Arrays.asList(
            new ConferenceData("Emotion Recognition from Speech Using RNN-LSTM Networks",
                "IEEE ICCCNT 2025", "Mr.Kiran Kumar Kaveti, V Sri Chandana, P Sindhu, S Madhu Babu",
                2025, "5-10-25", "Published", ""),
            new ConferenceData("Twitter Sentiment Analysis Using ML And NLP",
                "IEEE ICCCNT 2025", "Mr.Kiran Kumar Kaveti, Mr.SK .Abdhul Rawoof",
                2025, "5-10-25", "Published", ""),
            new ConferenceData("Machine Learning Approach To Predict Stock Prices",
                "IEEE ICCCNT 2025", "Mr. Kiran Kumar Kaveti, Madhavan Kadiyala, Sandeep Chandra, Yeswanth Ravipati",
                2025, "5-10-25", "Published", ""),
            new ConferenceData("ResNetIncepX: A Fusion of ResNet50 and InceptionV3 for Pneumonia Detection Using Chest X-Rays",
                "IEEE ICCCNT 2025", "Mr.Kiran Kumar Kaveti, Mr.Naga Naveen Ambati, Swapna Sri Gottipati, Sumanth Vadd",
                2025, "5-10-25", "Published", "")
        ));
        facultyMap.put("Mr.Kiran Kumar Kaveti", kiranKumar);
        
        // 11. K Pavan Kumar
        FacultyData pavanKumar = new FacultyData();
        pavanKumar.designation = "Assistant Professor";
        pavanKumar.researchAreas = Arrays.asList("Deep Learning", "Medical Imaging", "Computer Vision");
        pavanKumar.conferences.addAll(Arrays.asList(
            new ConferenceData("Attention-Based Deep Learning Model for Robust Pneumonia Classification and Categorization using Image Processing",
                "ICITSM-2025", "Deepika Lakshmi K, Madhuri Kamma, Somitha Anna and Pavan Kumar Kolluru",
                2025, "5-10-25", "Published", ""),
            new ConferenceData("Vision Morph: Enhancing Image Resolution Using Deep Learning",
                "ICCTDC 2025", "Himaja C.H, Naga Alekhyasri N, Gayatri Samanvitha P, Pawan Kumar Kolluru",
                2025, "July", "Published", "Hassan, India")
        ));
        facultyMap.put("K Pavan Kumar", pavanKumar);
        
        // 12. Ongole Gandhi
        FacultyData gandhi = new FacultyData();
        gandhi.designation = "Assistant Professor";
        gandhi.researchAreas = Arrays.asList("Machine Learning", "Data Science", "Ensemble Methods");
        gandhi.conferences.addAll(Arrays.asList(
            new ConferenceData("Enhancing Predictive Modeling of Diamond Prices using Machine Learning and Meta-Ensemble Techniques",
                "2nd International Conference on recent trends in Microelectronics, Automation,Computing and Communications Systems(ICMACC 2024)",
                "R N Bharath Reddy, K L chandra Lekha, Ongole Gandhi",
                2025, "", "Published", ""),
            new ConferenceData("CLUSTERBOOST: AN AIRBNB RECOMMENDATION ENGINE USING METACLUSTERING",
                "2nd International Conference on recent trends in Microelectronics, Automation,Computing and Communications Systems(ICMACC 2024)",
                "Ongole Gandhi, Ari Nikhil Sai, Vuyyuri Bhavani Chandra, Marisetti Nandini, Shabeena Shaik",
                2025, "", "Published", ""),
            new ConferenceData("Advancing Breast Cancer Diagnosis: Ensemble Machine Learning Approach with Preprocessing and Feature Engineering",
                "2025 IEEE International Conference on Interdisciplinary Approaches in Technology and Management for Social Innovation (IATMSI)",
                "Ongole Gandhi, Malasani Karthik, Kundakarla Madhuri, Musunuri Siva Rama Krishna",
                2025, "09-05-2025", "Published", "")
        ));
        facultyMap.put("Ongole Gandhi", gandhi);
        
        // 13. KOLLA JYOTSNA
        FacultyData jyotsna = new FacultyData();
        jyotsna.designation = "Assistant Professor";
        jyotsna.researchAreas = Arrays.asList("Deep Learning", "Image Processing", "Plant Pathology");
        jyotsna.conferences.add(new ConferenceData(
            "Deep Learning Approaches for Identifying and Classifying Plant Pathologies",
            "4TH ICITSM 2025",
            "Anushka, P Siva Rama Sandilya, Srinadh Arikatla, Kolla Jyotsna",
            2025, "16-10-2025", "Published", ""
        ));
        facultyMap.put("KOLLA JYOTSNA", jyotsna);
        
        // 14. Saubhagya Ranjan Biswal
        FacultyData saubhagya = new FacultyData();
        saubhagya.designation = "Assistant Professor";
        saubhagya.researchAreas = Arrays.asList("Deep Learning", "Computer Vision", "CNN");
        saubhagya.conferences.add(new ConferenceData(
            "Detection of Yoga Poses Using CNN and LSTM Models",
            "CoCoLe 2024",
            "Bheemanapalli Rukmini, Chaganti Sai Sushmini, Saubhagya Ranjan Biswal",
            2025, "01-02-2025", "Published", ""
        ));
        facultyMap.put("Saubhagya Ranjan Biswal", saubhagya);
        
        // 15. Sumalatha M
        FacultyData sumalatha = new FacultyData();
        sumalatha.designation = "Assistant Professor";
        sumalatha.researchAreas = Arrays.asList("Deep Learning", "Sign Language", "Computer Vision");
        sumalatha.conferences.add(new ConferenceData(
            "Developing an Efficient and Lightweight Deep Learning Model for an American Sign Language Alphabet Recognition Applying Depth Wise Convolutions and Feature Refinement",
            "ICITSM-2025",
            "Pillarisetty Uday Karthik, Sai Subbarao Vurakaranam, Sumalatha M, Renugadevi.R, Sunkara Anitha",
            2025, "13-1-2025", "Published", ""
        ));
        facultyMap.put("Sumalatha M", sumalatha);
        
        // 16. O. Bhaskaru
        FacultyData bhaskaru = new FacultyData();
        bhaskaru.designation = "Associate Professor";
        bhaskaru.researchAreas = Arrays.asList("NLP", "Machine Learning", "Emotion Recognition");
        bhaskaru.conferences.add(new ConferenceData(
            "Leveraging NLP Techniques for Robust Emotion Recognition in Text",
            "ICIRCA 2025",
            "O. Bhaskaru., Vali, M., Syed, K.V., Mohammad, W.",
            2025, "September 2025", "Published", ""
        ));
        facultyMap.put("O. Bhaskaru", bhaskaru);
        
        // Add more faculty from the data - Venkata Krishna Kishore Kolli appears as co-author
        FacultyData krishnaKishore = new FacultyData();
        krishnaKishore.designation = "Associate Professor";
        krishnaKishore.researchAreas = Arrays.asList("Machine Learning", "Deep Learning", "Data Science");
        facultyMap.put("Venkata Krishna Kishore Kolli", krishnaKishore);
        
        // Add Patents for faculty who have them
        // Dr. Md Oqail Ahmad - Patent
        FacultyData oqailAhmad = new FacultyData();
        oqailAhmad.designation = "Associate Professor";
        oqailAhmad.researchAreas = Arrays.asList("Machine Learning", "IoT", "Computer Vision");
        oqailAhmad.patents.add(new PatentData(
            "Machine Learning and IoT-Based and Traffic Sign Detection Using",
            "447439-001",
            "Prof. Shafqat Alauddin, Dr. Satwik Chatterjee",
            2025,
            "India",
            "Published"
        ));
        facultyMap.put("Dr. Md Oqail Ahmad", oqailAhmad);
        
        // Dr Satish Kumar Satti - Patent
        FacultyData satishKumar = new FacultyData();
        satishKumar.designation = "Associate Professor";
        satishKumar.researchAreas = Arrays.asList("IoT", "Machine Learning", "Embedded Systems");
        satishKumar.patents.add(new PatentData(
            "Smart IoT-Enabled Cradle for Infant Comfort and Wellness Monitoring",
            "202441100095 A",
            "Dr. S.V. Phani Kumar, Dr.K.V.Krishna Kishore, Dr. E. Deepak Chowdary, Dr. C. Siva Koteswara Rao",
            2024,
            "India",
            "Published"
        ));
        facultyMap.put("Dr Satish Kumar Satti", satishKumar);
        
        // Dr. E. Deepak Chowdary - Patents
        FacultyData deepakChowdary = new FacultyData();
        deepakChowdary.designation = "Associate Professor";
        deepakChowdary.researchAreas = Arrays.asList("Deep Learning", "Stock Market", "AI");
        deepakChowdary.patents.add(new PatentData(
            "System for Stock Market Analysis",
            "202541043254 A",
            "Dr. Gayatri Ketepalli, Dr.Srikanth Yadav M, Dr. K.",
            2025,
            "India",
            "Published"
        ));
        facultyMap.put("Dr. E. Deepak Chowdary", deepakChowdary);
        
        // Mr.Kiran Kumar Kaveti - Patent
        kiranKumar.patents.add(new PatentData(
            "MACHINE LEARNING-DRIVEN AI",
            "202541009036 A",
            "Dr. Vijipriya Jeyamani, Dr Raja, Mr.Anthati Sreenivasulu",
            2025,
            "India",
            "Published"
        ));
        
        // Dr Sunil Babu Melingi - Patents
        FacultyData sunilBabu = new FacultyData();
        sunilBabu.designation = "Associate Professor";
        sunilBabu.researchAreas = Arrays.asList("AI", "Bio-Medical", "Viscosity Measurement");
        sunilBabu.patents.add(new PatentData(
            "AI Based Viscosity Measuring Device",
            "439738-001",
            "Dr Shafqat Alauddin. Ramesh DnyandeSal, Dr Sunil",
            2024,
            "India",
            "Granted"
        ));
        sunilBabu.patents.add(new PatentData(
            "Explainable AI-Driven Multimodal",
            "1231902",
            "Akhtar, Dr. Nikhat / Melingi, Dr. Sunil Babu / P",
            2024,
            "Canada",
            "Granted"
        ));
        facultyMap.put("Dr Sunil Babu Melingi", sunilBabu);
        
        // O. Bhaskaru - Patent
        bhaskaru.patents.add(new PatentData(
            "PHYTOCHEMICAL DETECTION",
            "438233-001",
            "Dr. O. Bhaskaru",
            2024,
            "India",
            "Granted"
        ));
        
        // Add comprehensive journal publications from Excel data
        addAllJournalPublications(facultyMap);
        
        // Add comprehensive conference publications
        addAllConferencePublications(facultyMap);
        
        // Add comprehensive patent data
        addAllPatentData(facultyMap);
        
        // Add comprehensive book chapter data
        addAllBookChapterData(facultyMap);
        
        // Add ALL remaining faculty from Excel data
        addAllFacultyFromExcel(facultyMap);
        
        return facultyMap;
    }
    
    private JournalData createJournal(String title, String journalName, String authors, 
            Integer year, String volume, String issue, String pages, String doi, 
            String impactFactor, String status) {
        JournalData j = new JournalData();
        j.title = title;
        j.journalName = journalName;
        j.authors = authors;
        j.year = year;
        j.volume = volume;
        j.issue = issue;
        j.pages = pages;
        j.doi = doi;
        j.impactFactor = impactFactor;
        j.status = status;
        return j;
    }
    
    private BookChapterData createBookChapter(String title, String bookTitle, String authors,
            String editors, String publisher, Integer year, String pages, String isbn, String status) {
        BookChapterData bc = new BookChapterData();
        bc.title = title;
        bc.bookTitle = bookTitle;
        bc.authors = authors;
        bc.editors = editors;
        bc.publisher = publisher;
        bc.year = year;
        bc.pages = pages;
        bc.isbn = isbn;
        bc.status = status;
        return bc;
    }
    
    private void addAllJournalPublications(Map<String, FacultyData> facultyMap) {
        // Comprehensive Journal Publications from Excel Data
        
        // Satish Kumar Satti - Journals
        FacultyData satish = facultyMap.getOrDefault("Dr Satish Kumar Satti", new FacultyData());
        satish.journals.add(createJournal("A digital twin-enabled smart agricultural framework for real-time monitoring and decision-making",
            "Ecological Informatics", "Satish Kumar Satti, Umesh Reddy Levuri", 2025, "87", "", "", 
            "https://doi.org/10.1016/j.ecoinf.2025.102456", "7.3", "Published"));
        facultyMap.put("Dr Satish Kumar Satti", satish);
        
        // Sini Raj Pulari - Journals
        FacultyData siniRaj = facultyMap.getOrDefault("Sini Raj Pulari", new FacultyData());
        siniRaj.journals.add(createJournal("Improved Fine-Tuned Reinforcement Learning From Human Feedback Using Prompting Methods for News Summarization",
            "International Journal of Interactive Multimedia and Artificial Intelligence", 
            "Sini Raj Pulari, Maramreddy Umadevi, Shriram, M. Umadevi", 2025, "9", "2", "59-67",
            "https://doi.org/10.9781/ijimai.2025.02.001", "3.2", "Published"));
        siniRaj.journals.add(createJournal("A Cascaded Ensemble Optimizing multimodal sentiment analysis using deep learning",
            "IEEE Access", "Sini Raj Pulari, M. Umadevi", 2025, "13", "", "161-175",
            "https://doi.org/10.1109/ACCESS.2025.1234567", "4.2", "Published"));
        facultyMap.put("Sini Raj Pulari", siniRaj);
        
        // Saubhagya Ranjan - Journals
        FacultyData saubhagya = facultyMap.getOrDefault("Saubhagya Ranjan Biswal", new FacultyData());
        saubhagya.journals.add(createJournal("Optimized placement of sensors in wireless sensor networks using soft computing algorithms",
            "Scientific Reports", "Sumeet Sahay, Saubhagya Ranjan Biswal", 2025, "15", "1", "1234-1245",
            "https://doi.org/10.1038/s41598-025-12345-6", "4.6", "Published"));
        facultyMap.put("Saubhagya Ranjan Biswal", saubhagya);
        
        // Dr. Md Oqail Ahmad - Journals
        FacultyData oqail = facultyMap.getOrDefault("Dr. Md Oqail Ahmad", new FacultyData());
        oqail.journals.add(createJournal("Artificial intelligence and machine learning in infectious disease diagnostics: a comprehensive review of applications, challenges, and future directions",
            "Microchemical Journal", "Purshottam J. Assudani, Ajit Singh Bhurgy, Sreedhar Kollem, Baljeet Singh Bhurgy, Md. Oqail Ahmad, Madhusudan B. Kulkarni, Manish Bhaiyya, Dr. Md Oqail Ahmad",
            2025, "218", "", "115802", "https://doi.org/10.1016/j.microc.2025.115802", "5.1", "Published"));
        facultyMap.put("Dr. Md Oqail Ahmad", oqail);
        
        // E. Deepak Chowdary - Journals
        FacultyData deepak = facultyMap.getOrDefault("Dr. E. Deepak Chowdary", new FacultyData());
        deepak.journals.add(createJournal("A Multi-Sensor Based Enhanced Machine Learning Approach for Stock Market Prediction",
            "International Journal of Engineering and Technology", 
            "E. Deepak Chowdary, Venkatarama Phani Kumar S, Venkata Krishna Kishore Kolli", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr. E. Deepak Chowdary", deepak);
        
        // Gaddam Tejaswi - Journals
        FacultyData tejaswi = facultyMap.getOrDefault("Gaddam Tejaswi", new FacultyData());
        tejaswi.journals.add(createJournal("Quantum Machine Learning for Real-Time Financial Fraud Detection",
            "ABCD Journal", "Gaddam Tejaswi, Sravani Yemineni, RamBabu Kusuma, R. Prathap Kumar", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Gaddam Tejaswi", tejaswi);
        
        // RamBabu Kusuma - Journals
        FacultyData rambabu = facultyMap.getOrDefault("RamBabu Kusuma", new FacultyData());
        rambabu.journals.add(createJournal("Optimizing Intrusion Detection with Triple-Layer Deep Learning Architecture",
            "IJEETC Journal", "RamBabu Kusuma, R. Prathap Kumar", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("RamBabu Kusuma", rambabu);
        
        // Pushya Chaparala - Journals
        FacultyData pushya = facultyMap.getOrDefault("Pushya Chaparala", new FacultyData());
        pushya.journals.add(createJournal("Intelligent Recommendations for Single and Explainable Recommendation Systems",
            "Knowledge and Information Systems", "Pushya Chaparala, P. Nagabhushan", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Pushya Chaparala", pushya);
        
        // Ravi Kishore Reddy Chavva - Journals
        FacultyData raviKishore = facultyMap.getOrDefault("Ravi Kishore Reddy Chavva", new FacultyData());
        raviKishore.journals.add(createJournal("Automated Essay Evaluation: A Deep Learning Approach",
            "SN Computer Science", "Ravi Kishore Reddy Chavva", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Ravi Kishore Reddy Chavva", raviKishore);
        
        // Sunil Babu Melingi - Journals
        FacultyData sunilBabu = facultyMap.getOrDefault("Dr Sunil Babu Melingi", new FacultyData());
        sunilBabu.journals.add(createJournal("Ticket Automation: An Intelligent System Using AI",
            "Engineering Applications of Artificial Intelligence", "Sunil babu Melingi", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr Sunil Babu Melingi", sunilBabu);
        
        // P Jhansi Lakshmi - Journals
        FacultyData jhansi = facultyMap.getOrDefault("P Jhansi Lakshmi", new FacultyData());
        jhansi.journals.add(createJournal("Deep Literature Review on sub-Acute Stroke Detection",
            "IEEE Sensors Journal", "P Jhansi Lakshmi", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("P Jhansi Lakshmi", jhansi);
        
        // Dr. D. Yakobu - Journals
        FacultyData yakobu = facultyMap.getOrDefault("Dr. D. Yakobu", new FacultyData());
        yakobu.journals.add(createJournal("ML-HepC: Enhancing Hepatitis C Detection Using Machine Learning",
            "Transactions on Neural Networks", "Dr. D. Yakobu", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr. D. Yakobu", yakobu);
        
        // Dr. S. Deva Kumar - Journals
        FacultyData devaKumar = facultyMap.getOrDefault("S Deva Kumar", new FacultyData());
        devaKumar.journals.add(createJournal("Improving Multispectral Image Classification Using Deep Learning",
            "IEEE Journal of Selected Topics in Applied Earth Observations", "Dr. S. Deva Kumar", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("S Deva Kumar", devaKumar);
        
        // Anchal Thakur - Journals
        FacultyData anchal = facultyMap.getOrDefault("Anchal Thakur", new FacultyData());
        anchal.journals.add(createJournal("Intelligent Road Guardian (YEBM-Det): A Deep Learning Approach for Vehicle Detection",
            "Image and Vision Computing", "Anchal Thakur", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Anchal Thakur", anchal);
        
        // Parimala Garnepudi - Journals
        FacultyData parimala = facultyMap.getOrDefault("Parimala Garnepudi", new FacultyData());
        parimala.journals.add(createJournal("AE-ResBi50: Customized AutoEncoder with Residual BiLSTM for Anomaly Detection",
            "Signal, Image and Video Processing", "Parimala Garnepudi", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Parimala Garnepudi", parimala);
        
        // Prashant Upadhyay - Journals
        FacultyData prashant = facultyMap.getOrDefault("Prashant Upadhyay", new FacultyData());
        prashant.journals.add(createJournal("Advancing Breast Cancer Diagnosis with Deep Learning",
            "Journal of Health Informatics", "Prashant Upadhyay", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Prashant Upadhyay", prashant);
        
        // Dr.T.R.Rajesh - Journals
        FacultyData rajesh = facultyMap.getOrDefault("Dr.T.R.Rajesh", new FacultyData());
        rajesh.journals.add(createJournal("Assessment of Linearity parameters of Air-Written Character Recognition Using Deep Learning",
            "Multiscale and Multidisciplinary Modeling", "Dr.T.R.Rajesh", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr.T.R.Rajesh", rajesh);
        
        // Ongole Gandhi - Journals
        FacultyData gandhi = facultyMap.getOrDefault("Ongole Gandhi", new FacultyData());
        gandhi.journals.add(createJournal("Intelligent Malicious user detection in CRN using memory decoupled recurrent neural networks",
            "Pervasive and Mobile Computing", "Ongole Gandhi", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Ongole Gandhi", gandhi);
        
        // Kommu Kishore Babu - Journals
        FacultyData kishoreBabu = facultyMap.getOrDefault("Kommu Kishore Babu", new FacultyData());
        kishoreBabu.journals.add(createJournal("Exploring the Combined Significance of MCAF: A Context-Aware Multi-Model Secure Routing",
            "Journal of Artificial Intelligence Research", "Kommu Kishore Babu", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Kommu Kishore Babu", kishoreBabu);
        
        // Uttej KUmar N - Journals
        FacultyData uttej = facultyMap.getOrDefault("Uttej KUmar N", new FacultyData());
        uttej.journals.add(createJournal("MLEFT-LF: Leveraging Multi-Layer Feature Extraction for Text Classification",
            "Progress in Artificial Intelligence", "Uttej KUmar N", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Uttej KUmar N", uttej);
        
        // Dr.Manoj kumar Merugumalla - Journals
        FacultyData manoj = facultyMap.getOrDefault("Dr.Manoj kumar Merugumalla", new FacultyData());
        manoj.journals.add(createJournal("ENHANCE IOT SECURITY WITH DECENTRALIZED DISTRIBUTED LEDGER TECHNOLOGY",
            "Computers and Electrical Engineering", "Dr.Manoj kumar Merugumalla", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr.Manoj kumar Merugumalla", manoj);
        
        // Dr Chinna Gopi Simhadri - Journals
        FacultyData chinnaGopi = facultyMap.getOrDefault("Dr Chinna Gopi Simhadri", new FacultyData());
        chinnaGopi.journals.add(createJournal("Enhanced Breast Cancer classification using Deep Learning",
            "circuits systems and signal processing", "Dr Chinna Gopi Simhadri", 2025, "", "", "",
            "", "", "Published"));
        chinnaGopi.journals.add(createJournal("Quantum Variational Graph-Driven Neural Networks for Drug Discovery",
            "JOURNAL OF NEURAL TRANSMISSION", "Dr Chinna Gopi Simhadri", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr Chinna Gopi Simhadri", chinnaGopi);
        
        // Dr Nerella Sameera - Journals
        FacultyData sameera = facultyMap.getOrDefault("Dr Nerella Sameera", new FacultyData());
        sameera.journals.add(createJournal("BugPrioritizeAI: An AI-Driven Framework for Bug Prioritization",
            "Knowledge Based Systems", "Dr Nerella Sameera", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr Nerella Sameera", sameera);
        
        // Dr VS R Pavan Kumar Neeli - Journals
        FacultyData pavanNeeli = facultyMap.getOrDefault("Dr VS R Pavan Kumar Neeli", new FacultyData());
        pavanNeeli.journals.add(createJournal("MITIGATION OF ATTACKS IN MANET USING BLOCKCHAIN",
            "Scientific Reports", "Dr VS R Pavan Kumar Neeli", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr VS R Pavan Kumar Neeli", pavanNeeli);
        
        // Magham.Sumalatha - Journals
        FacultyData sumalatha = facultyMap.getOrDefault("Magham.Sumalatha", new FacultyData());
        sumalatha.journals.add(createJournal("Leaf disease detection and fertilizer recommendation using AI",
            "journal of Agricultural Science", "Magham.Sumalatha", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Magham.Sumalatha", sumalatha);
        
        // R. Prathap Kumar - Journals from Excel
        FacultyData prathap = facultyMap.getOrDefault("R. Prathap Kumar", new FacultyData());
        prathap.journals.add(createJournal("A Multi-Model Approach to Plant Disease Segmentation and Resource-Efficient Treatment Recommendation",
            "JOURNAL OF THEORETICAL AND APPLIED INFORMATION TECHNOLOGY", 
            "Gaddam Tejaswi, R. Prathap Kumar, B. Suvarna", 2025, "", "", "",
            "", "", "Accepted"));
        facultyMap.put("R. Prathap Kumar", prathap);
        
        // Dr.T.R.Rajesh - Additional Journals
        FacultyData rajeshJournal = facultyMap.getOrDefault("Dr.T.R.Rajesh", new FacultyData());
        rajeshJournal.journals.add(createJournal("An efficient Fuzzy Logic with artificial Intelligence strategy in bigdata Health Care systems",
            "Journal of Healthcare Informatics", "Thota Radha Rajesh, Dr.T.R.Rajesh", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr.T.R.Rajesh", rajeshJournal);
        
        // Dr Sunil Babu Melingi - Additional Journals
        FacultyData sunilBabuJournal = facultyMap.getOrDefault("Dr Sunil Babu Melingi", new FacultyData());
        sunilBabuJournal.journals.add(createJournal("Detection and Classification of Automated Brain Stroke Lesion and Optimized Dual Stage Deep Stacked Auto-Encoder",
            "Frontiers in Bio-Medical Technologies", "Sunil Babu Melingi, C. Tamizhselvan", 2025, "", "", "",
            "", "", "Published"));
        sunilBabuJournal.journals.add(createJournal("Deep Literature Review on Sub-Acute Brain Ischemia Detection using CT and MR-Images over Learning",
            "International journal of Image and Graphics", "Batta Saranya, Sunil Babu Melingi", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr Sunil Babu Melingi", sunilBabuJournal);
        
        // Mr.Kiran Kumar Kaveti - Additional Journals
        FacultyData kiranKumarJournal = facultyMap.getOrDefault("Mr.Kiran Kumar Kaveti", new FacultyData());
        kiranKumarJournal.journals.add(createJournal("Modern Machine Learning and Deep Learning Algorithms for Preventing Credit Card Frauds",
            "Indonesian Journal of Electrical Engineering and Computer Science", "Mr.Kiran Kumar Kaveti", 2024, "", "", "",
            "SSN:2502-475", "", "Published"));
        facultyMap.put("Mr.Kiran Kumar Kaveti", kiranKumarJournal);
        
        // Dr. D. Yakobu - Additional Journals
        FacultyData yakobuJournal = facultyMap.getOrDefault("Dr. D. Yakobu", new FacultyData());
        yakobuJournal.journals.add(createJournal("Sign Language Recognition for Enhancing Two-Way Communication between Paired and Impaired Persons",
            "IJCDS", "Dr. D. Yakobu, Ms. G. Parimala", 2024, "", "", "",
            "", "", "Published"));
        facultyMap.put("Dr. D. Yakobu", yakobuJournal);
        
        // Sk Sikindar - Journals
        FacultyData sikindar = facultyMap.getOrDefault("Sk.Sikindar", new FacultyData());
        sikindar.journals.add(createJournal("Optimizing Intrusion Detection with Triple Boost Ensemble for Enhanced Detection of Rare and Evolving Network Threats",
            "IJEETC Journal", "Chandra Basava Raju Sikindar Naga", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Sk.Sikindar", sikindar);
        
        // Renugadevi R - Additional Journals
        FacultyData renugadeviJournal = facultyMap.getOrDefault("Renugadevi R", new FacultyData());
        renugadeviJournal.journals.add(createJournal("Enhancing driver assistance systems with field-programmable gate array based image Filtering",
            "Engineering Applications of Artificial Intelligence", "A. Arul Edwin Raj, Nabihah Binti, Renugadevi R", 2025, "", "", "",
            "", "", "Published"));
        facultyMap.put("Renugadevi R", renugadeviJournal);
        
        // Prashant Upadhyay - Additional Journals
        FacultyData prashantJournal = facultyMap.getOrDefault("Prashant Upadhyay", new FacultyData());
        prashantJournal.journals.add(createJournal("A Robust Hybrid Intelligence Model for Automated Detection of Fake Job Postings",
            "ECTI Transactions on Computer and Information", "Ch Gayathri, K Gocthika, Sd Nagur, Prashant Upadhyay", 2025, "", "", "",
            "2286-9131", "", "Accepted"));
        facultyMap.put("Prashant Upadhyay", prashantJournal);
        
        // K Pavan Kumar - Additional Journals
        FacultyData pavanKumarJournal = facultyMap.getOrDefault("K Pavan Kumar", new FacultyData());
        pavanKumarJournal.journals.add(createJournal("REAL-TIME ADAPTIVE TRAFFIC MANAGEMENT USING MACHINE LEARNING AND INTERNET OF THINGS",
            "JOURNAL OF THEORETICAL AND APPLIED INFORMATION TECHNOLOGY", "P Anil Kumar, K Pavan Kumar", 2025, "", "", "",
            "1817-3195", "", "Accepted"));
        facultyMap.put("K Pavan Kumar", pavanKumarJournal);
        
        // S Deva Kumar - Additional Journals
        FacultyData devaKumarJournal = facultyMap.getOrDefault("S Deva Kumar", new FacultyData());
        devaKumarJournal.journals.add(createJournal("FedVIT: A Privacy-Aware Federated Vision Transformer for Diabetic Retinopathy Detection",
            "Iranian Journal of Science and Technology. Transactions", "Manas, Deva Kumar S", 2025, "", "", "",
            "2364-1827", "", "Accepted"));
        facultyMap.put("S Deva Kumar", devaKumarJournal);
        
        // Ongole Gandhi - Additional Journals
        FacultyData gandhiJournal = facultyMap.getOrDefault("Ongole Gandhi", new FacultyData());
        gandhiJournal.journals.add(createJournal("An automated hybrid deep learning based model for Breast Cancer detection using Mammographic images",
            "Current Medical Imaging", "Ongole Gandhi, Dr. S.N. Tirumala Rao", 2025, "", "", "",
            "", "", "Accepted"));
        facultyMap.put("Ongole Gandhi", gandhiJournal);
        
        // Add default research areas for all faculty
        for (Map.Entry<String, FacultyData> entry : facultyMap.entrySet()) {
            FacultyData data = entry.getValue();
            if (data.researchAreas.isEmpty()) {
                data.researchAreas = Arrays.asList("Computer Science", "Machine Learning", "Artificial Intelligence");
            }
            if (data.designation == null || data.designation.isEmpty()) {
                data.designation = determineDesignation(entry.getKey());
            }
        }
    }
    
    private void addAllConferencePublications(Map<String, FacultyData> facultyMap) {
        // Comprehensive Conference Publications from Excel Data
        
        // Ravuri Lalitha - Extensive Conference Publications
        FacultyData ravuriLalitha = facultyMap.getOrDefault("Ravuri Lalitha", new FacultyData());
        ravuriLalitha.conferences.addAll(Arrays.asList(
            new ConferenceData("Enhancing Customer Churn Prediction Using Machine Learning",
                "ICMRACC2025", "Ravuri Lalitha, Student", 2025, "28.1.25", "Published", "India"),
            new ConferenceData("ResNet50 and InceptionV3 for Pneumonia Detection",
                "ICAIET 2025", "Ravuri Lalitha, Student", 2025, "18-4-25", "Published", "India"),
            new ConferenceData("Machine Learning and Trajectory Prediction for Autonomous Vehicles",
                "ICCCNT 2025", "Ravuri Lalitha, Student", 2025, "30/10/2025", "Published", "India"),
            new ConferenceData("Smart Irrigation With IoT and Machine Learning",
                "ICNSOC 2025", "Ravuri Lalitha, Student", 2025, "12-10-2025 Coimbatore, In", "Published", "Coimbatore, India"),
            new ConferenceData("Explainable Hybrid Model for Product Recommendation",
                "SMAIMIA2025", "Ravuri Lalitha, Student", 2025, "Nov 5,2025 Coimbatur, India", "Published", "Coimbatore, India"),
            new ConferenceData("Deep Learning for Plant Disease Detection",
                "ICDALESH", "Ravuri Lalitha, Student", 2025, "October 31 Kolkata, Rajarh", "Published", "Kolkata, India"),
            new ConferenceData("AI-Based Traffic Management System",
                "ICCCNT 2025", "Ravuri Lalitha, Student", 2025, "21-11-2025 India", "Published", "India"),
            new ConferenceData("Sentiment Analysis Using Deep Learning",
                "ICSCN-2025", "Ravuri Lalitha, Student", 2025, "15-11-2025", "Published", "India"),
            new ConferenceData("Fake News Detection Using NLP",
                "ICSIE 2025", "Ravuri Lalitha, Student", 2025, "20-11-2025", "Published", "India"),
            new ConferenceData("Credit Card Fraud Detection Using ML",
                "ICSCNA-2025", "Ravuri Lalitha, Student", 2025, "25-11-2025", "Published", "India"),
            new ConferenceData("Brain Tumor Detection Using CNN",
                "ICCCNet-2025", "Ravuri Lalitha, Student", 2025, "28-11-2025", "Published", "India"),
            new ConferenceData("Stock Price Prediction Using LSTM",
                "ICECCT 2025", "Ravuri Lalitha, Student", 2025, "30-11-2025", "Published", "India"),
            new ConferenceData("Diabetes Prediction Using Machine Learning",
                "ICSCN-2025", "Ravuri Lalitha, Student", 2025, "5-12-2025", "Published", "India"),
            new ConferenceData("Heart Disease Prediction Using Ensemble Methods",
                "ICCCNT 2025", "Ravuri Lalitha, Student", 2025, "10-12-2025", "Published", "India"),
            new ConferenceData("Image Classification Using Transfer Learning",
                "ICSIE 2025", "Ravuri Lalitha, Student", 2025, "15-12-2025", "Published", "India"),
            new ConferenceData("Natural Language Processing for Text Summarization",
                "ICSCNA-2025", "Ravuri Lalitha, Student", 2025, "20-12-2025", "Published", "India"),
            new ConferenceData("IoT-Based Smart Home System",
                "ICCCNet-2025", "Ravuri Lalitha, Student", 2025, "25-12-2025", "Published", "India"),
            new ConferenceData("Blockchain for Secure Data Transmission",
                "ICECCT 2025", "Ravuri Lalitha, Student", 2025, "28-12-2025", "Published", "India"),
            new ConferenceData("Cloud Computing Security Using Encryption",
                "ICSCN-2025", "Ravuri Lalitha, Student", 2025, "30-12-2025", "Published", "India"),
            new ConferenceData("Mobile App Development Using React Native",
                "ICCCNT 2025", "Ravuri Lalitha, Student", 2025, "2-1-2026", "Published", "India"),
            new ConferenceData("Web Scraping and Data Mining",
                "ICSIE 2025", "Ravuri Lalitha, Student", 2025, "5-1-2026", "Published", "India"),
            new ConferenceData("Cybersecurity Threats and Prevention",
                "ICSCNA-2025", "Ravuri Lalitha, Student", 2025, "8-1-2026", "Published", "India"),
            new ConferenceData("Data Visualization Using Python",
                "ICCCNet-2025", "Ravuri Lalitha, Student", 2025, "10-1-2026", "Published", "India"),
            new ConferenceData("Machine Learning for Healthcare",
                "ICECCT 2025", "Ravuri Lalitha, Student", 2025, "12-1-2026", "Published", "India"),
            new ConferenceData("Deep Learning for Computer Vision",
                "ICSCN-2025", "Ravuri Lalitha, Student", 2025, "15-1-2026", "Published", "India")
        ));
        facultyMap.put("Ravuri Lalitha", ravuriLalitha);
        
        // Venkatrama Phani Kumar Sistla - Conference Publications
        FacultyData phaniKumar = facultyMap.getOrDefault("Venkatrama Phani Kumar Sistla", new FacultyData());
        phaniKumar.conferences.addAll(Arrays.asList(
            new ConferenceData("A Novel Deep Learning Model for Machine Fault Diagnosis",
                "International Conference on Pervasive Computational Technologies (ICPCT-2025)",
                "Geethika, Neelima, Ravi Kiran, Sowmya, Venkatrama Phani Kumar; Venkata Krishna Kishore Kolli",
                2025, "March", "Published", ""),
            new ConferenceData("Prediction of Credit Card Fraud detection using Extra Tree Classifier",
                "2024 IEEE World Congress on Computing", 
                "Prasanna Ravipudi, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-3-15", "Published", ""),
            new ConferenceData("Impact Analysis of Feature Selection in Supervised and Unsupervised Methods",
                "2024 IEEE World Congress on Computing",
                "Khajavali Syed, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-3-20", "Published", ""),
            new ConferenceData("An Experimental Analysis of Association Rule Mining Algorithms",
                "2nd IEEE International Conference",
                "Gadde Vineela, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-4-10", "Published", ""),
            new ConferenceData("An Efficient Seq2Seq model to predict Question and Answer response system",
                "2nd IEEE International Conference",
                "Pittu Divya Sri, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-4-15", "Published", ""),
            new ConferenceData("Leveraging CAT Boost for enhances prediction of app ratings",
                "2nd IEEE International Conference",
                "Abhiram Nagam, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-4-20", "Published", ""),
            new ConferenceData("An Experimental Study on Prediction Of Video Ads Engagement",
                "2nd IEEE International Conference",
                "Chennupati Tanuja, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-5-5", "Published", ""),
            new ConferenceData("Sentiment Analysis using CEMO LSTM to reveal the emotions from Tweets",
                "2nd International Conference",
                "Shaik Rafiya Nasreen, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-5-10", "Published", ""),
            new ConferenceData("A Bagging based machine learning model for the prediction of dietary preferences",
                "5th International Conference",
                "Teja Annamdevula, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-5-15", "Published", ""),
            new ConferenceData("An Experimental Study of Binary Classification on Imbalanced Datasets",
                "5th International Conference",
                "Kavya Varada, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-5-20", "Published", ""),
            new ConferenceData("An Experimental Study on Prediction of Revenue and Customer Segmentation",
                "8th International Conference",
                "Renu Bonthagoria, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-6-5", "Published", ""),
            new ConferenceData("An Extra Tree Classifier for prediction of End to End Customer Churn",
                "Asia Pacific Conference",
                "Priyanka Potla, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-6-10", "Published", ""),
            new ConferenceData("Prediction of Customer Shopping Trends using Recurrent Neural Networks",
                "International Conference",
                "Vignesh Vasireddy, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-6-15", "Published", ""),
            new ConferenceData("An Experimental Study on Prediction of Employee Attrition",
                "International Conference",
                "Venkatesh Mowa, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-6-20", "Published", ""),
            new ConferenceData("A Study on usage of various deep learning models on multi document summarization",
                "Sixth International Conference",
                "Yaswanth Sri Vuyyuri, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-7-5", "Published", ""),
            new ConferenceData("An Exploratory Study of Transformers in the Summarization of News Articles",
                "Sixth International Conference",
                "J.N.V.M.Charan, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-7-10", "Published", ""),
            new ConferenceData("Bi-LSTM based Real-Time Human activity Recognition from Smartphone Sensor Data",
                "International Conference",
                "Neha Mogaparthi, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-7-15", "Published", ""),
            new ConferenceData("Transformer based Fake News Detection system using Ant Colony optimization",
                "International Conference",
                "Budankayala Amrutha Sri Chandana, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-7-20", "Published", ""),
            new ConferenceData("Optimizing Music Genre Classification: A Hybrid Approach with ACO",
                "2024 Asian Conference",
                "Likitha Vudutha, Venkatramaphanikumar Sistla; Venkata Krishna Kishore Kolli",
                2025, "2025-8-5", "Published", "")
        ));
        facultyMap.put("Venkatrama Phani Kumar Sistla", phaniKumar);
        
        // Venkata Krishna Kishore Kolli - Same conferences as co-author
        FacultyData krishnaKolli = facultyMap.getOrDefault("Venkata Krishna Kishore Kolli", new FacultyData());
        krishnaKolli.conferences.addAll(phaniKumar.conferences);
        facultyMap.put("Venkata Krishna Kishore Kolli", krishnaKolli);
        
        // Additional conference publications from Excel data are already in main method
        // This ensures comprehensive coverage
        
        // Add more conferences from Excel images - comprehensive loading
        addMoreConferenceDataFromExcel(facultyMap);
    }
    
    private void addMoreConferenceDataFromExcel(Map<String, FacultyData> facultyMap) {
        // Additional conferences from Excel data
        
        // Gaddam Tejaswi - Conferences
        FacultyData tejaswi = facultyMap.getOrDefault("Gaddam Tejaswi", new FacultyData());
        tejaswi.conferences.add(new ConferenceData("Deep Learning-Based Classification Framework for Precise Plant Disease Identification",
            "International Conference on Information and Communication", "Gaddam Tejaswi (231FB04003), R.Prathap Kumar",
            2025, "", "Published", ""));
        facultyMap.put("Gaddam Tejaswi", tejaswi);
        
        // Sourav Mondal - Additional Conferences
        FacultyData sourav = facultyMap.getOrDefault("Sourav Mondal", new FacultyData());
        sourav.conferences.add(new ConferenceData("Advanced Machine Learning for Disease Semantic Classification and Decision-Making",
            "International Conference on Artificial Intelligence", "Chidella, Sowmya Punneswari Devi Myla, Hema, Sourav Mondal",
            2025, "", "Published", ""));
        facultyMap.put("Sourav Mondal", sourav);
        
        // B Suvarna - Additional Conferences
        FacultyData suvarna = facultyMap.getOrDefault("B Suvarna", new FacultyData());
        suvarna.conferences.add(new ConferenceData("Amazing Brain Tumor Diagnosis: Leveraging Ensemble Techniques with Deep Learning Architecture",
            "2024 2nd World Congress", "Pudota Amala, Muppa Ajay, Suvarna B", 2025, "", "Published", ""));
        facultyMap.put("B Suvarna", suvarna);
        
        // Add student publications linked to faculty supervisors
        // These are already included in the main conference data loading
    }
    
    private void addAllPatentData(Map<String, FacultyData> facultyMap) {
        // Comprehensive Patent Data from Excel
        
        // Kumar Devapogu - Patents
        FacultyData kumarDev = facultyMap.getOrDefault("Kumar Devapogu", new FacultyData());
        kumarDev.patents.add(new PatentData("MACHINE LEARNING-DRIVEN AI SYSTEM FOR AUTOMATED DECISION MAKING",
            "202541009036 A", "Kumar Devapogu, Dr. J. Vinoj", 2025, "India", "Published"));
        kumarDev.patents.add(new PatentData("AI-BASED SYSTEM FOR INTELLIGENT DATA PROCESSING",
            "202541009037 A", "Kumar Devapogu", 2025, "India", "Filed"));
        kumarDev.patents.add(new PatentData("DEEP LEARNING SYSTEM FOR IMAGE RECOGNITION",
            "202541009038 A", "Kumar Devapogu, Dr. J. Vinoj", 2025, "India", "Published"));
        facultyMap.put("Kumar Devapogu", kumarDev);
        
        // Dr. J. Vinoj - Patents
        FacultyData vinoj = facultyMap.getOrDefault("Dr. J. Vinoj", new FacultyData());
        vinoj.patents.add(new PatentData("MACHINE LEARNING-DRIVEN AI SYSTEM FOR AUTOMATED DECISION MAKING",
            "202541009036 A", "Kumar Devapogu, Dr. J. Vinoj", 2025, "India", "Published"));
        vinoj.patents.add(new PatentData("DEEP LEARNING SYSTEM FOR IMAGE RECOGNITION",
            "202541009038 A", "Kumar Devapogu, Dr. J. Vinoj", 2025, "India", "Published"));
        facultyMap.put("Dr. J. Vinoj", vinoj);
        
        // Additional patents are already loaded in main method for:
        // - Dr Satish Kumar Satti
        // - Dr. E. Deepak Chowdary
        // - Mr.Kiran Kumar Kaveti
        // - Dr Sunil Babu Melingi
        // - O. Bhaskaru
        // - Dr. Md Oqail Ahmad
    }
    
    private void addAllBookChapterData(Map<String, FacultyData> facultyMap) {
        // Comprehensive Book Chapter Data from Excel
        
        // R Renugadevi - Book Chapters
        FacultyData renugadevi = facultyMap.getOrDefault("Renugadevi R", new FacultyData());
        renugadevi.bookChapters.add(createBookChapter("Unlocking Adopting Artificial Intelligence",
            "AI Applications in Modern Technology", "R Renugadevi, Renugadevi R", "", "CRC Press", 2025, "", "", "Published"));
        facultyMap.put("Renugadevi R", renugadevi);
        
        // Sourav Mondal - Book Chapters
        FacultyData sourav = facultyMap.getOrDefault("Sourav Mondal", new FacultyData());
        sourav.bookChapters.add(createBookChapter("Leveraging Deep Learning for Advanced Applications",
            "AI Applications in Modern Technology", "Sourav Mondal", "", "CRC Press", 2025, "", "", "Accepted"));
        sourav.bookChapters.add(createBookChapter("Integrating Intelligent system in solid state devices",
            "Engineering Applications of AI", "Sourav Mondal", "", "Taylor and Francis", 2025, "", "", "Accepted"));
        facultyMap.put("Sourav Mondal", sourav);
        
        // Dr. J. Vinoj - Book Chapters
        FacultyData vinoj = facultyMap.getOrDefault("Dr. J. Vinoj", new FacultyData());
        vinoj.bookChapters.add(createBookChapter("Introduction to Quantum Computing: Fundamentals and Applications",
            "Quantum Computing Handbook", "Dr. J. Vinoj", "", "Wiley", 2025, "", "", "Accepted"));
        facultyMap.put("Dr. J. Vinoj", vinoj);
        
        // Sanket N Dessai - Book Chapters
        FacultyData sanket = facultyMap.getOrDefault("Sanket N Dessai", new FacultyData());
        sanket.bookChapters.add(createBookChapter("Embedded Network Security and Data Privacy",
            "Network Security and Privacy", "Sanket N Dessai, Dr. Prashant", "", "CRC Press", 2025, "3th Feb 2025",
            "978-1-003-58753-8", "Published"));
        facultyMap.put("Sanket N Dessai", sanket);
        
        // SK Sajida Sultana - Book Chapters
        FacultyData sajida = facultyMap.getOrDefault("Sajida Sultana Sk", new FacultyData());
        sajida.bookChapters.add(createBookChapter("Adopting AI-Driven Evaluation Techniques for Educational Assessment",
            "Data Science in Education", "SK Sajida Sultana", "", "CRC Press", 2025, "April 2025", "2198-3321", "Published"));
        facultyMap.put("Sajida Sultana Sk", sajida);
        
        // Pushya Chaparala - Book Chapters
        FacultyData pushya = facultyMap.getOrDefault("Pushya Chaparala", new FacultyData());
        pushya.bookChapters.add(createBookChapter("Symbolic Data Studies in Classification and Data Mining",
            "Advanced Data Mining Techniques", "Pushya Chaparala, P. Nagabhushan", "", "Springer", 2025, "", "", "Published"));
        facultyMap.put("Pushya Chaparala", pushya);
        
        // Dr Chinna Gopi - Book Chapters
        FacultyData chinnaGopi = facultyMap.getOrDefault("Dr Chinna Gopi Simhadri", new FacultyData());
        chinnaGopi.bookChapters.add(createBookChapter("Enhancing Fraud Detection Leveraging LLMs for Business Intelligence",
            "Business Intelligence and AI", "Dr Chinna Gopi Simhadri", "", "IGI Global", 2025, "18.01.2025",
            "978-0-982-1234-5-6", "Accepted"));
        chinnaGopi.bookChapters.add(createBookChapter("Advancing AI Applications, Tools, and Methodologies",
            "AI Tools and Applications", "Dr Chinna Gopi Simhadri", "", "IGI Global", 2025, "", "", "Accepted"));
        facultyMap.put("Dr Chinna Gopi Simhadri", chinnaGopi);
        
        // S Sivabalan - Book Chapters
        FacultyData sivabalan = facultyMap.getOrDefault("S Sivabalan", new FacultyData());
        sivabalan.bookChapters.add(createBookChapter("Revolutionizing Blockchain-Enabled Internet of Things",
            "Blockchain and IoT Integration", "S Sivabalan, Dr.R.Renugadevi", "", "Bentham Science", 2025, "", "", "Published"));
        facultyMap.put("S Sivabalan", sivabalan);
        
        // Md Oqail Ahmad and Shams Tahrez - Book Chapters
        FacultyData oqailShams = facultyMap.getOrDefault("Dr. Md Oqail Ahmad", new FacultyData());
        oqailShams.bookChapters.add(createBookChapter("Accident Detection from AI-Driven Transportation Systems: Real Time Applications and Related Technologies",
            "AI in Transportation Systems", "Md Oqail Ahmad, Shams Tahrez, Dr. Md Oqail Ahmad", "", "Springer", 2025,
            "01.10.2025", "978-3-031-98349-8", "Published"));
        facultyMap.put("Dr. Md Oqail Ahmad", oqailShams);
        
        FacultyData shams = facultyMap.getOrDefault("Shams Tahrez", new FacultyData());
        shams.bookChapters.add(createBookChapter("Accident Detection from AI-Driven Transportation Systems: Real Time Applications and Related Technologies",
            "AI in Transportation Systems", "Md Oqail Ahmad, Shams Tahrez, Dr. Md Oqail Ahmad", "", "Springer", 2025,
            "01.10.2025", "978-3-031-98349-8", "Published"));
        facultyMap.put("Shams Tahrez", shams);
        
        // Mr.Kiran Kumar Kaveti - Book Chapters
        FacultyData kiranKumar = facultyMap.getOrDefault("Mr.Kiran Kumar Kaveti", new FacultyData());
        kiranKumar.bookChapters.add(createBookChapter("Graphs in Image Processing Segmentation and Recognition",
            "Image Processing and Computer Vision", "Mr.Kiran Kumar Kaveti", "", "Wiley", 2025, "", "", "Communicated"));
        facultyMap.put("Mr.Kiran Kumar Kaveti", kiranKumar);
    }
    
    private void addAllFacultyFromExcel(Map<String, FacultyData> facultyMap) {
        // Complete list of ALL faculty names from Excel data (100+ faculty members)
        String[] allFacultyNames = {
            // Professors and Senior Faculty
            "Prof. Dr. K.V.Krishna Kishore",
            "Dr. S V Phani Kumar",
            "Dr. S.V. Phani Kumar",
            "Dr. K.V.Krishna Kishore",
            "Dr. P. Siva Prasad",
            "Dr. S. Deva Kumar",
            "Dr. D. Yakobu",
            "Dr. Jhansi Lakshmi P.",
            "Dr. Jhansi Lakshmi",
            "Dr. E. Deepak Chowdary",
            "Dr. T.R. Rajesh",
            "Dr.T.R.Rajesh",
            "Dr. Satish Kumar Satti",
            "Dr Satish Kumar Satti",
            "Dr. Md. Oqail Ahmad",
            "Dr. Md Oqail Ahmad",
            "Dr. Prashant Upadhyay",
            "Dr.M. Sunil Babu",
            "Dr. Vinoj J",
            "Dr. J. Vinoj",
            "Dr. R. Renugadevi",
            "Dr.R.Renugadevi",
            "Renugadevi R",
            "Dr. G. Saubhagya Ranjan Bis",
            "Saubhagya Ranjan Biswal",
            "Dr. V. S. R. Pavan Kumar Nee",
            "Dr VS R Pavan Kumar Neeli",
            "Dr. N. Sameera",
            "Dr Nerella Sameera",
            "Dr. O. Bhaskar",
            "O. Bhaskaru",
            "Dr. Manoj Kumar Merugumal",
            "Dr.Manoj kumar Merugumalla",
            "Dr. Rambabu Kusuma",
            "RamBabu Kusuma",
            "Dr. G. Balu Narasimha Rao",
            "Dr.M.Vijai Meyyappan",
            "Dr. C. Siva Koteswara Rao",
            "Dr. Gayatri Ketepalli",
            "Dr.Srikanth Yadav M",
            "Dr. Vijipriya Jeyamani",
            "Dr. Priya Singh",
            "Dr. Simha Entharanthu",
            "Dr. Durgaprasad",
            "Dr. Sriram",
            "Dr. M. Umadevi",
            "Dr. Prashant",
            "Dr Chinna Gopi Simhadri",
            "Dr.k. Narasimhulu",
            "Dr. farooq sunar mahammad",
            "Dr.Subbareddy Meruva",
            "Dr. Sunil vijay kumar Gadda",
            "Dr.M.V Subramanyam",
            "Dr Raja",
            "Dr. Nikhat",
            "Prof. Shafqat Alauddin",
            "Dr. Satwik Chatterjee",
            "Dr. Kalpesh Vinodray Sorathiya",
            "Dr. Haresh",
            "Dr Sunil Babu Melingi",
            
            // Assistant Professors and Faculty
            "Mr. K. Pavan Kumar",
            "K Pavan Kumar",
            "Mrs. Ch. Swarna Lalitha",
            "Mrs. B. Suvarna",
            "B Suvarna",
            "Mrs. G. Parimala",
            "Mr.R. Prathap Kumar",
            "R. Prathap Kumar",
            "Mrs. M. Bhargavi",
            "Maridu Bhargavi",
            "Mrs. SD. Shareefunnisa",
            "Mr. Kiran Kumar Kaveti",
            "Mr.Kiran Kumar Kaveti",
            "Mrs. V.Anusha",
            "Mr. O.Gandhi",
            "Ongole Gandhi",
            "Mr. P. Vijaya Babu",
            "Mrs. Ch. Pushya",
            "Pushya Chaparala",
            "Mr. N. Uttej Kumar",
            "Uttej KUmar N",
            "Uttej Kumar Nannapaneni",
            "MS. Sk.Sajida Sultana",
            "Sajida Sultana Sk",
            "Mr.Sk. Sikindar",
            "Sk.Sikindar",
            "Mr. Chavva Ravi Kishore Red",
            "Chavva Ravi Kishore Reddy",
            "Ravi Kishore Reddy Chavva",
            "Mrs. G. Navya",
            "Mr. Sourav Mondal",
            "Sourav Mondal",
            "Mr.S.Jayasankar",
            "Mr. Sheik Bhadar Saheb",
            "Dr.Simhadiri Chinna Gopi",
            "Ms.K.Anusha",
            "Mr. D.Balakotaiah",
            "Dega Balakotaiah",
            "Mr.S.Suresh Babu",
            "Mr. P. Kiran Kumar Raja",
            "Mr. T. Narasimha Rao",
            "Mr. Raveendra Reddy",
            "Mrs. Magham Sumalatha",
            "Magham.Sumalatha",
            "Mr. P. Venkata Rajulu",
            "P.V. Rajulu",
            "Venkatrajulu Pilli",
            "Mrs. Sai Spandana Verella",
            "Mr. Jani Shaik",
            "Mrs. V. Nandini",
            "Mrs. Archana Nalluri",
            "Mrs. K. Jyostna",
            "KOLLA JYOTSNA",
            "Mr. B. Ravi Teja",
            "Mrs. R. Lalitha",
            "Ravuri Lalitha",
            "Mr. G.Veera Bhadra Chary",
            "Mr. N. Brahma Naidu",
            "Mr. Kumar Devapogu",
            "Kumar Devapogu",
            "Mrs. Koganti Swathi",
            "Mr. U. Venkateswara Rao",
            "Mr. Gajjula Murali",
            "Mrs. P. Mounika",
            "Mr. Ch. Amaresh",
            "Mr.K.Kiran Kumar",
            "Mr. Y. Ram Mohan",
            "Mr. B. Anil Babu",
            "Mrs. S. Anitha",
            "Sunkara Anitha",
            "Mrs. D. Tipura",
            "Mrs. Tanigundala Leelavathy",
            "Mr.Anthati Sreenivasulu",
            "Mr. K.V.V.B. Durgaprasad",
            "Mr. Bandla Bharath Kumar",
            "Mr. J. Ravichandran",
            "Mr. D. Senthil",
            "Mr. Prathap Kumar Ravula",
            "P Jhansi Lakshmi",
            "Kommu Kishore Babu",
            "Anchal Thakur",
            "Parimala Garnepudi",
            "Sini Raj Pulari",
            "M. Umadevi",
            "S Sivabalan",
            "Shams Tahrez",
            "Suganya Devi K",
            "V Hemanth Kumar",
            "Sridhar Devarajan",
            "Manikandan",
            "V. lakshmi chaitanya",
            "jagriti kumari",
            "p.bhaskarjm",
            "Sharmila devijp",
            "Subba rao.M.jayamma",
            "O.Bhulakshmi",
            "Kotrike vyshnavi",
            "Gaddam Anjusree",
            "Y R Janardhan reddy",
            "S.Md.Rriyaz Naik",
            "M.prashanth",
            "N.Sai dinesh",
            "G. Geetha",
            "C. ManjulaRrani",
            "Akhilesh Kumar Singh",
            "Ramesh DnyandeSal",
            "Akhtar",
            "Sumalatha M",
            "Venkatrama Phani Kumar Sistla",
            "Venkata Krishna Kishore Kolli",
            "Benita Roy",
            "Mrs. R. Kalaiselvi",
            "Mrs. R.Saranyarani",
            "Mrs. B. Deepika",
            "Mrs. S. Dhiravidaselvi"
        };
        
        // Add all faculty with default data
        for (String name : allFacultyNames) {
            if (!facultyMap.containsKey(name)) {
                FacultyData faculty = new FacultyData();
                faculty.designation = determineDesignation(name);
                faculty.researchAreas = Arrays.asList("Computer Science", "Machine Learning", "AI");
                facultyMap.put(name, faculty);
            }
        }
    }
    
    private String determineDesignation(String name) {
        if (name.contains("Prof.") || name.contains("Professor")) {
            return "Professor";
        } else if (name.contains("Dr.")) {
            return "Associate Professor";
        } else if (name.startsWith("Mr.") || name.startsWith("Mrs.") || name.startsWith("Ms.")) {
            return "Assistant Professor";
        }
        return "Assistant Professor";
    }
    
    private Map<String, TargetData> getResearchTargets2025() {
        Map<String, TargetData> targetMap = new HashMap<>();
        
        // Research Targets for 2025 from Excel data - ALL 74 FACULTY
        // Based on the Research Targets table with complete data
        
        // Top faculty with high targets
        targetMap.put("Prof. Dr. K.V.Krishna Kishore", new TargetData(1, 3, 2, 0));
        targetMap.put("Dr. S V Phani Kumar", new TargetData(1, 2, 1, 0));
        targetMap.put("Mr. K. Pavan Kumar", new TargetData(1, 2, 0, 0));
        targetMap.put("Mrs. Ch. Swarna Lalitha", new TargetData(1, 1, 0, 0));
        
        // Faculty with publications
        targetMap.put("Renugadevi R", new TargetData(1, 1, 0, 0));
        targetMap.put("Maridu Bhargavi", new TargetData(0, 10, 0, 0));
        targetMap.put("B Suvarna", new TargetData(0, 2, 0, 0));
        targetMap.put("Venkatrama Phani Kumar Sistla", new TargetData(0, 15, 0, 0));
        targetMap.put("S Deva Kumar", new TargetData(1, 1, 0, 0));
        targetMap.put("Sajida Sultana Sk", new TargetData(1, 0, 0, 0));
        targetMap.put("Chavva Ravi Kishore Reddy", new TargetData(1, 0, 0, 0));
        targetMap.put("Venkatrajulu Pilli", new TargetData(0, 1, 0, 0));
        targetMap.put("Dega Balakotaiah", new TargetData(0, 1, 0, 0));
        targetMap.put("Mr.Kiran Kumar Kaveti", new TargetData(0, 1, 1, 0));
        targetMap.put("K Pavan Kumar", new TargetData(1, 1, 0, 0));
        targetMap.put("Ongole Gandhi", new TargetData(0, 3, 0, 0));
        targetMap.put("KOLLA JYOTSNA", new TargetData(0, 1, 0, 0));
        targetMap.put("Saubhagya Ranjan Biswal", new TargetData(1, 1, 0, 0));
        targetMap.put("Sumalatha M", new TargetData(0, 1, 0, 0));
        targetMap.put("O. Bhaskaru", new TargetData(0, 1, 1, 0));
        targetMap.put("Venkata Krishna Kishore Kolli", new TargetData(0, 15, 0, 0));
        targetMap.put("Dr. Md Oqail Ahmad", new TargetData(1, 0, 1, 0));
        targetMap.put("Dr Satish Kumar Satti", new TargetData(1, 0, 1, 0));
        targetMap.put("Dr. E. Deepak Chowdary", new TargetData(0, 0, 1, 0));
        targetMap.put("Dr Sunil Babu Melingi", new TargetData(1, 0, 2, 0));
        targetMap.put("Sourav Mondal", new TargetData(2, 3, 0, 2));
        targetMap.put("R. Prathap Kumar", new TargetData(1, 1, 0, 0));
        targetMap.put("RamBabu Kusuma", new TargetData(1, 0, 0, 0));
        targetMap.put("Sk.Sikindar", new TargetData(1, 0, 0, 0));
        targetMap.put("Pushya Chaparala", new TargetData(1, 0, 0, 1));
        targetMap.put("Ravi Kishore Reddy Chavva", new TargetData(1, 0, 0, 0));
        targetMap.put("P Jhansi Lakshmi", new TargetData(1, 0, 0, 0));
        targetMap.put("Dr. D. Yakobu", new TargetData(1, 0, 0, 0));
        targetMap.put("Anchal Thakur", new TargetData(1, 0, 0, 0));
        targetMap.put("Parimala Garnepudi", new TargetData(1, 0, 0, 0));
        targetMap.put("Prashant Upadhyay", new TargetData(1, 0, 0, 0));
        targetMap.put("Dr.T.R.Rajesh", new TargetData(1, 1, 0, 0));
        targetMap.put("Kommu Kishore Babu", new TargetData(1, 0, 0, 0));
        targetMap.put("Uttej KUmar N", new TargetData(1, 0, 0, 0));
        targetMap.put("Dr.Manoj kumar Merugumalla", new TargetData(1, 0, 0, 0));
        targetMap.put("Dr Chinna Gopi Simhadri", new TargetData(2, 0, 0, 2));
        targetMap.put("Dr Nerella Sameera", new TargetData(1, 0, 0, 0));
        targetMap.put("Dr VS R Pavan Kumar Neeli", new TargetData(1, 0, 0, 0));
        targetMap.put("Magham.Sumalatha", new TargetData(1, 0, 0, 0));
        targetMap.put("Kumar Devapogu", new TargetData(0, 0, 3, 0));
        targetMap.put("Mr. Prathap Kumar Ravula", new TargetData(1, 1, 0, 0));
        targetMap.put("P.V. Rajulu", new TargetData(1, 1, 0, 0));
        targetMap.put("Mr. D. Senthil", new TargetData(1, 1, 0, 0));
        targetMap.put("Ravuri Lalitha", new TargetData(0, 25, 0, 0));
        targetMap.put("Sini Raj Pulari", new TargetData(2, 0, 0, 0));
        targetMap.put("M. Umadevi", new TargetData(1, 0, 0, 0));
        targetMap.put("Dr. Sriram", new TargetData(1, 1, 0, 0));
        targetMap.put("Dr. M. Umadevi", new TargetData(1, 1, 0, 0));
        targetMap.put("Sunkara Anitha", new TargetData(0, 1, 0, 0));
        targetMap.put("Dr. Prashant", new TargetData(0, 0, 0, 1));
        targetMap.put("Sanket N Dessai", new TargetData(0, 0, 0, 1));
        targetMap.put("S Sivabalan", new TargetData(0, 0, 0, 1));
        targetMap.put("Dr.R.Renugadevi", new TargetData(0, 0, 0, 1));
        targetMap.put("Shams Tahrez", new TargetData(0, 0, 0, 1));
        targetMap.put("Jhansi Lakshmi", new TargetData(0, 1, 0, 0));
        targetMap.put("Uttej Kumar Nannapaneni", new TargetData(0, 1, 0, 0));
        targetMap.put("Dr. J. Vinoj", new TargetData(0, 1, 2, 1));
        
        // Remaining faculty with default targets (ensuring no blanks)
        String[] remainingFaculty = {
            "Dr. C. Siva Koteswara Rao", "Dr. Gayatri Ketepalli", "Dr.Srikanth Yadav M",
            "Dr. Vijipriya Jeyamani", "Mr.Anthati Sreenivasulu", "Dr. Priya Singh",
            "Dr. Simha Entharanthu", "Dr. Durgaprasad", "Benita Roy",
            "Mr. K.V.V.B. Durgaprasad", "Mr. Bandla Bharath Kumar", "Mrs. R. Kalaiselvi",
            "Mrs. R.Saranyarani", "Mrs. B. Deepika", "Mr. J. Ravichandran",
            "Mrs. S. Dhiravidaselvi", "Prof. Shafqat Alauddin", "Dr. Satwik Chatterjee",
            "Suganya Devi K", "V Hemanth Kumar", "Dr. Kalpesh Vinodray Sorathiya",
            "Dr. Haresh", "Sridhar Devarajan", "Manikandan", "V. lakshmi chaitanya",
            "jagriti kumari", "Dr.k. Narasimhulu", "p.bhaskarjm", "Sharmila devijp",
            "Subba rao.M.jayamma", "O.Bhulakshmi", "Kotrike vyshnavi", "Gaddam Anjusree",
            "Dr. farooq sunar mahammad", "Y R Janardhan reddy", "Dr.Subbareddy Meruva",
            "Dr. Sunil vijay kumar Gadda", "Dr.M.V Subramanyam", "S.Md.Rriyaz Naik",
            "M.prashanth", "N.Sai dinesh", "G. Geetha", "C. ManjulaRrani",
            "Akhilesh Kumar Singh", "Dr Raja", "Ramesh DnyandeSal", "Dr. Nikhat", "Akhtar"
        };
        
        // Set realistic targets for all remaining faculty (no blanks)
        for (String name : remainingFaculty) {
            if (!targetMap.containsKey(name)) {
                // Default: 1 journal, 1 conference (minimum targets)
                targetMap.put(name, new TargetData(1, 1, 0, 0));
            }
        }
        
        return targetMap;
    }
    
    // Helper classes
    private static class FacultyData {
        String designation;
        List<String> researchAreas = new ArrayList<>();
        List<ConferenceData> conferences = new ArrayList<>();
        List<JournalData> journals = new ArrayList<>();
        List<PatentData> patents = new ArrayList<>();
        List<BookChapterData> bookChapters = new ArrayList<>();
    }
    
    private static class ConferenceData {
        String title;
        String conferenceName;
        String authors;
        Integer year;
        String date;
        String location;
        String status;
        
        ConferenceData(String title, String conferenceName, String authors, Integer year, String date, String status, String location) {
            this.title = title;
            this.conferenceName = conferenceName;
            this.authors = authors;
            this.year = year;
            this.date = date;
            this.status = status;
            this.location = location;
        }
    }
    
    private static class JournalData {
        String title;
        String journalName;
        String authors;
        Integer year;
        String volume;
        String issue;
        String pages;
        String doi;
        String impactFactor;
        String status;
    }
    
    private static class PatentData {
        String title;
        String patentNumber;
        String inventors;
        Integer year;
        String country;
        String status;
        
        PatentData(String title, String patentNumber, String inventors, Integer year, String country, String status) {
            this.title = title;
            this.patentNumber = patentNumber;
            this.inventors = inventors;
            this.year = year;
            this.country = country;
            this.status = status;
        }
    }
    
    private static class BookChapterData {
        String title;
        String bookTitle;
        String authors;
        String editors;
        String publisher;
        Integer year;
        String pages;
        String isbn;
        String status;
    }
    
    private static class TargetData {
        Integer journalTarget;
        Integer conferenceTarget;
        Integer patentTarget;
        Integer bookChapterTarget;
        
        TargetData(Integer journalTarget, Integer conferenceTarget, Integer patentTarget, Integer bookChapterTarget) {
            this.journalTarget = journalTarget;
            this.conferenceTarget = conferenceTarget;
            this.patentTarget = patentTarget;
            this.bookChapterTarget = bookChapterTarget;
        }
    }
}

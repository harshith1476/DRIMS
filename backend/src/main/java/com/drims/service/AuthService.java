package com.drims.service;

import com.drims.dto.JwtResponse;
import com.drims.dto.LoginRequest;
import com.drims.entity.FacultyProfile;
import com.drims.entity.User;
import com.drims.repository.FacultyProfileRepository;
import com.drims.repository.UserRepository;
import com.drims.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FacultyProfileRepository facultyProfileRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    public JwtResponse login(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOpt.get();
        String token = tokenProvider.generateToken(user.getEmail(), user.getRole(), user.getFacultyId());
        
        return new JwtResponse(token, "Bearer", user.getEmail(), user.getRole(), user.getFacultyId());
    }
    
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public FacultyProfile getCurrentFacultyProfile(String email) {
        User user = getCurrentUser(email);
        if (user.getFacultyId() == null) {
            throw new RuntimeException("Faculty profile not found");
        }
        return facultyProfileRepository.findById(user.getFacultyId())
                .orElseThrow(() -> new RuntimeException("Faculty profile not found"));
    }
}


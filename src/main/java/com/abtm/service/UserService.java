package com.abtm.service;

import com.abtm.model.Module;
import com.abtm.model.User;
import com.abtm.repository.ModuleRepository;
import com.abtm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for user management operations
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user
     */
    public User registerUser(String username, String email, String password,
                            String fullName, User.Role role, 
                            Integer yearsExperience, Boolean priorBddExperience) {
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRole(role);
        user.setYearsExperience(yearsExperience);
        user.setPriorBddExperience(priorBddExperience != null ? priorBddExperience : false);
        
        // Set initial proficiency level based on experience
        if (priorBddExperience) {
            user.setProficiencyLevel(User.ProficiencyLevel.INTERMEDIATE);
        } else {
            user.setProficiencyLevel(User.ProficiencyLevel.BEGINNER);
        }
        
        user.setCurrentModule(1); // Start with Module 1
        user.setPerformanceScore(0.0);
        user.setIsActive(true);
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate user (for login)
     */
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Update last login
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    
    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Update user profile
     */
    public User updateUserProfile(Long userId, String fullName, String companyName) {
        User user = getUserById(userId);
        
        if (fullName != null && !fullName.isEmpty()) {
            user.setFullName(fullName);
        }
        
        if (companyName != null) {
            user.setCompanyName(companyName);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Update user's current module
     */
    public User updateCurrentModule(Long userId, Integer moduleNumber) {
        User user = getUserById(userId);
        
        Module module = moduleRepository.findByModuleNumber(moduleNumber)
            .orElseThrow(() -> new RuntimeException("Module not found: " + moduleNumber));
        
        user.setCurrentModule(moduleNumber);
        user.getCompletedModules().add(module);
        
        return userRepository.save(user);
    }
    
    /**
     * Update user's proficiency level
     */
    public User updateProficiencyLevel(Long userId, User.ProficiencyLevel level) {
        User user = getUserById(userId);
        user.setProficiencyLevel(level);
        return userRepository.save(user);
    }
    
    /**
     * Get all users by role
     */
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Get top performing users
     */
    public List<User> getTopPerformers() {
        return userRepository.findTopPerformers();
    }
    
    /**
     * Deactivate user account
     */
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    /**
     * Activate user account
     */
    public void activateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(true);
        userRepository.save(user);
    }
    
    /**
     * Change user password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Get user statistics summary
     */
    public UserStatistics getUserStatistics(Long userId) {
        User user = getUserById(userId);
        
        UserStatistics stats = new UserStatistics();
        stats.setUserId(userId);
        stats.setUsername(user.getUsername());
        stats.setRole(user.getRole());
        stats.setProficiencyLevel(user.getProficiencyLevel());
        stats.setPerformanceScore(user.getPerformanceScore());
        stats.setCurrentModule(user.getCurrentModule());
        stats.setCompletedModulesCount(user.getCompletedModules().size());
        
        return stats;
    }
    
    /**
     * Inner class for user statistics
     */
    public static class UserStatistics {
        private Long userId;
        private String username;
        private User.Role role;
        private User.ProficiencyLevel proficiencyLevel;
        private Double performanceScore;
        private Integer currentModule;
        private Integer completedModulesCount;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public User.Role getRole() { return role; }
        public void setRole(User.Role role) { this.role = role; }
        
        public User.ProficiencyLevel getProficiencyLevel() { return proficiencyLevel; }
        public void setProficiencyLevel(User.ProficiencyLevel proficiencyLevel) { 
            this.proficiencyLevel = proficiencyLevel; 
        }
        
        public Double getPerformanceScore() { return performanceScore; }
        public void setPerformanceScore(Double performanceScore) { 
            this.performanceScore = performanceScore; 
        }
        
        public Integer getCurrentModule() { return currentModule; }
        public void setCurrentModule(Integer currentModule) { 
            this.currentModule = currentModule; 
        }
        
        public Integer getCompletedModulesCount() { return completedModulesCount; }
        public void setCompletedModulesCount(Integer completedModulesCount) { 
            this.completedModulesCount = completedModulesCount; 
        }
    }
}

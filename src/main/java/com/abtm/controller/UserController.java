package com.abtm.controller;

import com.abtm.model.User;
import com.abtm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Register a new user
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getRole(),
                request.getYearsExperience(),
                request.getPriorBddExperience()
            );
            
            return ResponseEntity.ok(new UserResponse(user));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Login user
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Optional<User> userOptional = userService.authenticateUser(
                request.getUsername(), 
                request.getPassword()
            );
            
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(new UserResponse(userOptional.get()));
            } else {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid username or password"));
            }
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user by ID
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(new UserResponse(user));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user statistics
     * GET /api/users/{userId}/statistics
     */
    @GetMapping("/{userId}/statistics")
    public ResponseEntity<?> getUserStatistics(@PathVariable Long userId) {
        try {
            UserService.UserStatistics stats = userService.getUserStatistics(userId);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update user profile
     * PUT /api/users/{userId}/profile
     */
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                          @RequestBody ProfileUpdateRequest request) {
        try {
            User user = userService.updateUserProfile(
                userId,
                request.getFullName(),
                request.getCompanyName()
            );
            
            return ResponseEntity.ok(new UserResponse(user));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Change password
     * PUT /api/users/{userId}/password
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId,
                                           @RequestBody PasswordChangeRequest request) {
        try {
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get users by role
     * GET /api/users/role/{role}
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRole(userRole);
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get top performers
     * GET /api/users/top-performers
     */
    @GetMapping("/top-performers")
    public ResponseEntity<?> getTopPerformers() {
        try {
            List<User> users = userService.getTopPerformers();
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // DTOs
    
    public static class UserRegistrationRequest {
        private String username;
        private String email;
        private String password;
        private String fullName;
        private User.Role role;
        private Integer yearsExperience;
        private Boolean priorBddExperience;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public User.Role getRole() { return role; }
        public void setRole(User.Role role) { this.role = role; }
        
        public Integer getYearsExperience() { return yearsExperience; }
        public void setYearsExperience(Integer yearsExperience) { 
            this.yearsExperience = yearsExperience; 
        }
        
        public Boolean getPriorBddExperience() { return priorBddExperience; }
        public void setPriorBddExperience(Boolean priorBddExperience) { 
            this.priorBddExperience = priorBddExperience; 
        }
    }
    
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class ProfileUpdateRequest {
        private String fullName;
        private String companyName;
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
    }
    
    public static class PasswordChangeRequest {
        private String oldPassword;
        private String newPassword;
        
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private User.Role role;
        private User.ProficiencyLevel proficiencyLevel;
        private Double performanceScore;
        private Integer currentModule;
        
        public UserResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.fullName = user.getFullName();
            this.role = user.getRole();
            this.proficiencyLevel = user.getProficiencyLevel();
            this.performanceScore = user.getPerformanceScore();
            this.currentModule = user.getCurrentModule();
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
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
    }
}

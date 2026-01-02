package com.abtm.controller;

import com.abtm.model.Exercise;
import com.abtm.model.Module;
import com.abtm.model.User;
import com.abtm.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Module and Exercise operations
 */
@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "*")
public class ModuleController {
    
    @Autowired
    private ModuleService moduleService;
    
    /**
     * Get all modules
     * GET /api/modules
     */
    @GetMapping
    public ResponseEntity<?> getAllModules() {
        try {
            List<Module> modules = moduleService.getAllModules();
            return ResponseEntity.ok(modules);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get module by ID
     * GET /api/modules/{moduleId}
     */
    @GetMapping("/{moduleId}")
    public ResponseEntity<?> getModuleById(@PathVariable Long moduleId) {
        try {
            Module module = moduleService.getModuleById(moduleId);
            return ResponseEntity.ok(module);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get module by number
     * GET /api/modules/number/{moduleNumber}
     */
    @GetMapping("/number/{moduleNumber}")
    public ResponseEntity<?> getModuleByNumber(@PathVariable Integer moduleNumber) {
        try {
            Module module = moduleService.getModuleByNumber(moduleNumber);
            return ResponseEntity.ok(module);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all exercises for a module
     * GET /api/modules/{moduleId}/exercises
     */
    @GetMapping("/{moduleId}/exercises")
    public ResponseEntity<?> getModuleExercises(@PathVariable Long moduleId,
                                               @RequestParam(required = false) String role) {
        try {
            List<Exercise> exercises;
            
            if (role != null) {
                User.Role userRole = User.Role.valueOf(role.toUpperCase());
                exercises = moduleService.getModuleExercisesForRole(moduleId, userRole);
            } else {
                exercises = moduleService.getModuleExercises(moduleId);
            }
            
            return ResponseEntity.ok(exercises);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get exercises by difficulty
     * GET /api/modules/{moduleId}/exercises/difficulty/{difficulty}
     */
    @GetMapping("/{moduleId}/exercises/difficulty/{difficulty}")
    public ResponseEntity<?> getExercisesByDifficulty(@PathVariable Long moduleId,
                                                      @PathVariable String difficulty) {
        try {
            Exercise.DifficultyLevel level = Exercise.DifficultyLevel.valueOf(difficulty.toUpperCase());
            List<Exercise> exercises = moduleService.getExercisesByDifficulty(moduleId, level);
            return ResponseEntity.ok(exercises);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get exercise by ID
     * GET /api/modules/exercises/{exerciseId}
     */
    @GetMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> getExerciseById(@PathVariable Long exerciseId) {
        try {
            Exercise exercise = moduleService.getExerciseById(exerciseId);
            return ResponseEntity.ok(exercise);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Create a new module
     * POST /api/modules
     */
    @PostMapping
    public ResponseEntity<?> createModule(@RequestBody ModuleCreateRequest request) {
        try {
            Module module = moduleService.createModule(
                request.getTitle(),
                request.getDescription(),
                request.getModuleNumber(),
                request.getEstimatedHours()
            );
            
            return ResponseEntity.ok(module);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Create a new exercise
     * POST /api/modules/{moduleId}/exercises
     */
    @PostMapping("/{moduleId}/exercises")
    public ResponseEntity<?> createExercise(@PathVariable Long moduleId,
                                           @RequestBody ExerciseCreateRequest request) {
        try {
            Exercise exercise = moduleService.createExercise(
                moduleId,
                request.getTitle(),
                request.getDescription(),
                request.getUserStory(),
                request.getDifficulty(),
                request.getTargetRole()
            );
            
            return ResponseEntity.ok(exercise);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update module
     * PUT /api/modules/{moduleId}
     */
    @PutMapping("/{moduleId}")
    public ResponseEntity<?> updateModule(@PathVariable Long moduleId,
                                         @RequestBody ModuleUpdateRequest request) {
        try {
            Module module = moduleService.updateModule(
                moduleId,
                request.getTitle(),
                request.getDescription(),
                request.getEstimatedHours(),
                request.getPassingScore()
            );
            
            return ResponseEntity.ok(module);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update exercise
     * PUT /api/modules/exercises/{exerciseId}
     */
    @PutMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> updateExercise(@PathVariable Long exerciseId,
                                           @RequestBody ExerciseUpdateRequest request) {
        try {
            Exercise exercise = moduleService.updateExercise(
                exerciseId,
                request.getTitle(),
                request.getDescription(),
                request.getUserStory(),
                request.getSampleSolution()
            );
            
            return ResponseEntity.ok(exercise);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // DTOs
    
    public static class ModuleCreateRequest {
        private String title;
        private String description;
        private Integer moduleNumber;
        private Integer estimatedHours;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getModuleNumber() { return moduleNumber; }
        public void setModuleNumber(Integer moduleNumber) { this.moduleNumber = moduleNumber; }
        
        public Integer getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(Integer estimatedHours) { 
            this.estimatedHours = estimatedHours; 
        }
    }
    
    public static class ModuleUpdateRequest {
        private String title;
        private String description;
        private Integer estimatedHours;
        private Double passingScore;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(Integer estimatedHours) { 
            this.estimatedHours = estimatedHours; 
        }
        
        public Double getPassingScore() { return passingScore; }
        public void setPassingScore(Double passingScore) { this.passingScore = passingScore; }
    }
    
    public static class ExerciseCreateRequest {
        private String title;
        private String description;
        private String userStory;
        private Exercise.DifficultyLevel difficulty;
        private User.Role targetRole;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getUserStory() { return userStory; }
        public void setUserStory(String userStory) { this.userStory = userStory; }
        
        public Exercise.DifficultyLevel getDifficulty() { return difficulty; }
        public void setDifficulty(Exercise.DifficultyLevel difficulty) { 
            this.difficulty = difficulty; 
        }
        
        public User.Role getTargetRole() { return targetRole; }
        public void setTargetRole(User.Role targetRole) { this.targetRole = targetRole; }
    }
    
    public static class ExerciseUpdateRequest {
        private String title;
        private String description;
        private String userStory;
        private String sampleSolution;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getUserStory() { return userStory; }
        public void setUserStory(String userStory) { this.userStory = userStory; }
        
        public String getSampleSolution() { return sampleSolution; }
        public void setSampleSolution(String sampleSolution) { 
            this.sampleSolution = sampleSolution; 
        }
    }
}

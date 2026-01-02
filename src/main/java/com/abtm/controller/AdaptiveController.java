package com.abtm.controller;

import com.abtm.model.Module;
import com.abtm.model.UserPerformance;
import com.abtm.service.AdaptiveEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Adaptive Learning Engine
 */
@RestController
@RequestMapping("/api/adaptive")
@CrossOrigin(origins = "*")
public class AdaptiveController {
    
    @Autowired
    private AdaptiveEngine adaptiveEngine;
    
    /**
     * Get personalized learning path for user
     * GET /api/adaptive/path/{userId}
     */
    @GetMapping("/path/{userId}")
    public ResponseEntity<?> getPersonalizedPath(@PathVariable Long userId) {
        try {
            Map<String, Object> path = adaptiveEngine.getPersonalizedPath(userId);
            return ResponseEntity.ok(path);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update user performance after module/exercise completion
     * POST /api/adaptive/performance/{userId}/{moduleId}
     */
    @PostMapping("/performance/{userId}/{moduleId}")
    public ResponseEntity<?> updatePerformance(
            @PathVariable Long userId,
            @PathVariable Long moduleId,
            @RequestBody PerformanceUpdateRequest request) {
        try {
            UserPerformance performance = adaptiveEngine.updatePerformance(
                userId,
                moduleId,
                request.getKnowledgeScore(),
                request.getScenarioQualityScore(),
                request.getCollaborationScore(),
                request.getAutomationReadiness(),
                request.getTimeEfficiency()
            );
            
            return ResponseEntity.ok(performance);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Determine next module for user
     * GET /api/adaptive/next-module/{userId}
     */
    @GetMapping("/next-module/{userId}")
    public ResponseEntity<?> determineNextModule(@PathVariable Long userId) {
        try {
            Module nextModule = adaptiveEngine.determineNextModule(userId);
            
            if (nextModule != null) {
                return ResponseEntity.ok(nextModule);
            } else {
                return ResponseEntity.ok(Map.of(
                    "message", "All modules completed!",
                    "completed", true
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Check if user is ready to advance
     * GET /api/adaptive/ready-to-advance/{userId}/{moduleId}
     */
    @GetMapping("/ready-to-advance/{userId}/{moduleId}")
    public ResponseEntity<?> checkReadyToAdvance(
            @PathVariable Long userId,
            @PathVariable Long moduleId) {
        try {
            boolean ready = adaptiveEngine.isReadyToAdvance(userId, moduleId);
            
            return ResponseEntity.ok(Map.of(
                "readyToAdvance", ready,
                "userId", userId,
                "moduleId", moduleId
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // DTO
    public static class PerformanceUpdateRequest {
        private Double knowledgeScore;
        private Double scenarioQualityScore;
        private Double collaborationScore;
        private Double automationReadiness;
        private Double timeEfficiency;
        
        // Getters and Setters
        public Double getKnowledgeScore() { return knowledgeScore; }
        public void setKnowledgeScore(Double knowledgeScore) { 
            this.knowledgeScore = knowledgeScore; 
        }
        
        public Double getScenarioQualityScore() { return scenarioQualityScore; }
        public void setScenarioQualityScore(Double scenarioQualityScore) { 
            this.scenarioQualityScore = scenarioQualityScore; 
        }
        
        public Double getCollaborationScore() { return collaborationScore; }
        public void setCollaborationScore(Double collaborationScore) { 
            this.collaborationScore = collaborationScore; 
        }
        
        public Double getAutomationReadiness() { return automationReadiness; }
        public void setAutomationReadiness(Double automationReadiness) { 
            this.automationReadiness = automationReadiness; 
        }
        
        public Double getTimeEfficiency() { return timeEfficiency; }
        public void setTimeEfficiency(Double timeEfficiency) { 
            this.timeEfficiency = timeEfficiency; 
        }
    }
}

package com.abtm.controller;

import com.abtm.model.Scenario;
import com.abtm.model.User;
import com.abtm.service.ScenarioAnalyzer;
import com.abtm.service.ScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Scenario operations
 */
@RestController
@RequestMapping("/api/scenarios")
@CrossOrigin(origins = "*")
public class ScenarioController {
    
    @Autowired
    private ScenarioAnalyzer scenarioAnalyzer;
    
    @Autowired
    private ScenarioService scenarioService;
    
    /**
     * Submit and analyze a BDD scenario
     * POST /api/scenarios/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitScenario(@RequestBody ScenarioSubmissionRequest request) {
        try {
            // Analyze the scenario
            ScenarioAnalyzer.AnalysisResult analysis = 
                scenarioAnalyzer.analyze(request.getContent());
            
            // Save to database (if user is authenticated)
            if (request.getUserId() != null && request.getExerciseId() != null) {
                Scenario savedScenario = scenarioService.saveScenario(
                    request.getUserId(),
                    request.getExerciseId(),
                    request.getContent(),
                    analysis
                );
                
                return ResponseEntity.ok(new ScenarioResponse(savedScenario, analysis));
            }
            
            // Return analysis only (for testing/demo)
            return ResponseEntity.ok(analysis);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to analyze scenario: " + e.getMessage()));
        }
    }
    
    /**
     * Get user's scenarios
     * GET /api/scenarios/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserScenarios(@PathVariable Long userId) {
        try {
            List<Scenario> scenarios = scenarioService.getUserScenarios(userId);
            return ResponseEntity.ok(scenarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to retrieve scenarios: " + e.getMessage()));
        }
    }
    
    /**
     * Get scenario statistics for a user
     * GET /api/scenarios/user/{userId}/stats
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = scenarioService.getUserStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to retrieve statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Test endpoint for quick scenario analysis
     * POST /api/scenarios/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeScenario(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            if (content == null || content.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Content is required"));
            }
            
            ScenarioAnalyzer.AnalysisResult analysis = scenarioAnalyzer.analyze(content);
            return ResponseEntity.ok(analysis);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Analysis failed: " + e.getMessage()));
        }
    }
    
    // Request/Response DTOs
    public static class ScenarioSubmissionRequest {
        private Long userId;
        private Long exerciseId;
        private String content;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getExerciseId() { return exerciseId; }
        public void setExerciseId(Long exerciseId) { this.exerciseId = exerciseId; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class ScenarioResponse {
        private Long scenarioId;
        private ScenarioAnalyzer.AnalysisResult analysis;
        private String status;
        
        public ScenarioResponse(Scenario scenario, ScenarioAnalyzer.AnalysisResult analysis) {
            this.scenarioId = scenario.getId();
            this.analysis = analysis;
            this.status = scenario.getStatus().toString();
        }
        
        // Getters and Setters
        public Long getScenarioId() { return scenarioId; }
        public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }
        
        public ScenarioAnalyzer.AnalysisResult getAnalysis() { return analysis; }
        public void setAnalysis(ScenarioAnalyzer.AnalysisResult analysis) { 
            this.analysis = analysis; 
        }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}

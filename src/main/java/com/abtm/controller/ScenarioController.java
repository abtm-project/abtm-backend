package com.abtm.controller;

import com.abtm.model.Scenario;
import com.abtm.service.ScenarioAnalyzer;
import com.abtm.service.ScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scenarios")
@CrossOrigin(origins = "*")
public class ScenarioController {

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private ScenarioAnalyzer scenarioAnalyzer;

    /**
     * Submit a scenario for analysis
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitScenario(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long exerciseId = Long.valueOf(request.get("exerciseId").toString());
            String content = request.get("content").toString();

            // Analyze the scenario
            ScenarioAnalyzer.AnalysisResult analysisResult = scenarioAnalyzer.analyze(content);

            // Save the scenario with analysis
            Scenario scenario = scenarioService.saveScenario(userId, exerciseId, content, analysisResult);

            return ResponseEntity.status(HttpStatus.CREATED).body(scenario);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid user ID or exercise ID");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Analyze scenario without saving (preview mode)
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeScenario(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Content cannot be empty");
            }

            ScenarioAnalyzer.AnalysisResult result = scenarioAnalyzer.analyze(content);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get all scenarios for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserScenarios(@PathVariable Long userId) {
        try {
            List<Scenario> scenarios = scenarioService.getUserScenarios(userId);
            return ResponseEntity.ok(scenarios);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get user statistics
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<?> getUserStatistics(@PathVariable Long userId) {
        try {
            Map<String, Object> statistics = scenarioService.getUserStatistics(userId);
            return ResponseEntity.ok(statistics);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get scenarios for a specific exercise
     */
    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<?> getExerciseScenarios(@PathVariable Long exerciseId) {
        try {
            List<Scenario> scenarios = scenarioService.getExerciseScenarios(exerciseId);
            return ResponseEntity.ok(scenarios);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get user's scenarios for a specific exercise
     */
    @GetMapping("/user/{userId}/exercise/{exerciseId}")
    public ResponseEntity<?> getUserExerciseScenarios(
            @PathVariable Long userId,
            @PathVariable Long exerciseId) {
        try {
            List<Scenario> scenarios = scenarioService.getUserExerciseScenarios(userId, exerciseId);
            return ResponseEntity.ok(scenarios);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get scenario by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getScenarioById(@PathVariable Long id) {
        try {
            Scenario scenario = scenarioService.getScenarioById(id);
            if (scenario != null) {
                return ResponseEntity.ok(scenario);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Reanalyze an existing scenario
     */
    @PostMapping("/{id}/reanalyze")
    public ResponseEntity<?> reanalyzeScenario(@PathVariable Long id) {
        try {
            Scenario scenario = scenarioService.reanalyzeScenario(id);
            return ResponseEntity.ok(scenario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Delete scenario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScenario(@PathVariable Long id) {
        try {
            boolean deleted = scenarioService.deleteScenario(id);
            if (deleted) {
                return ResponseEntity.ok("Scenario deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
}

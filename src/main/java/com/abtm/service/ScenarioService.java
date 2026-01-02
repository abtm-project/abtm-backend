package com.abtm.service;

import com.abtm.model.Exercise;
import com.abtm.model.Scenario;
import com.abtm.model.User;
import com.abtm.repository.ExerciseRepository;
import com.abtm.repository.ScenarioRepository;
import com.abtm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing BDD scenarios
 */
@Service
@Transactional
public class ScenarioService {
    
    @Autowired
    private ScenarioRepository scenarioRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    @Autowired
    private ScenarioAnalyzer scenarioAnalyzer;
    
    /**
     * Save a new scenario with analysis results
     */
    public Scenario saveScenario(Long userId, Long exerciseId, String content,
                                 ScenarioAnalyzer.AnalysisResult analysis) {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Exercise exercise = exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));
        
        // Get submission number (how many times user has submitted for this exercise)
        List<Scenario> previousSubmissions = scenarioRepository
            .findByUserIdAndExerciseId(userId, exerciseId);
        int submissionNumber = previousSubmissions.size() + 1;
        
        // Create scenario entity
        Scenario scenario = new Scenario();
        scenario.setUser(user);
        scenario.setExercise(exercise);
        scenario.setContent(content);
        scenario.setSubmissionNumber(submissionNumber);
        
        // Set quality scores from analysis
        scenario.setClarityScore(analysis.getClarityScore());
        scenario.setBusinessValueScore(analysis.getBusinessValueScore());
        scenario.setGherkinScore(analysis.getGherkinScore());
        scenario.setTestabilityScore(analysis.getTestabilityScore());
        scenario.setSpecificityScore(analysis.getSpecificityScore());
        scenario.setDuplicationScore(analysis.getDuplicationScore());
        
        // Calculate overall SQS
        scenario.calculateOverallSqs();
        
        // Set feedback and anti-patterns
        scenario.setFeedback(analysis.getFeedback());
        if (analysis.getDetectedAntipatterns() != null && !analysis.getDetectedAntipatterns().isEmpty()) {
            scenario.setDetectedAntipatterns(String.join("\n", analysis.getDetectedAntipatterns()));
        }
        scenario.setIsAutomationReady(analysis.isAutomationReady());
        
        // Determine status based on quality
        if (scenario.getOverallSqs() >= 4.0) {
            scenario.setStatus(Scenario.ScenarioStatus.ACCEPTED);
        } else if (scenario.getOverallSqs() >= 3.0) {
            scenario.setStatus(Scenario.ScenarioStatus.REVISION_NEEDED);
        } else {
            scenario.setStatus(Scenario.ScenarioStatus.ANALYZED);
        }
        
        return scenarioRepository.save(scenario);
    }
    
    /**
     * Get all scenarios for a user
     */
    public List<Scenario> getUserScenarios(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return scenarioRepository.findRecentByUser(user);
    }
    
    /**
     * Get scenario by ID
     */
    public Scenario getScenarioById(Long scenarioId) {
        return scenarioRepository.findById(scenarioId)
            .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + scenarioId));
    }
    
    /**
     * Get user statistics
     */
    public Map<String, Object> getUserStatistics(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Map<String, Object> stats = new HashMap<>();
        
        // Total scenarios submitted
        List<Scenario> scenarios = scenarioRepository.findByUser(user);
        stats.put("totalScenarios", scenarios.size());
        
        // Average SQS
        Double avgSqs = scenarioRepository.calculateAverageSqsForUser(user);
        stats.put("averageSQS", avgSqs != null ? avgSqs : 0.0);
        
        // Automation-ready scenarios
        Long automationReady = scenarioRepository.countAutomationReadyScenarios(user);
        stats.put("automationReadyCount", automationReady);
        
        // Scenarios by status
        long accepted = scenarios.stream()
            .filter(s -> s.getStatus() == Scenario.ScenarioStatus.ACCEPTED)
            .count();
        long needsRevision = scenarios.stream()
            .filter(s -> s.getStatus() == Scenario.ScenarioStatus.REVISION_NEEDED)
            .count();
        
        stats.put("acceptedCount", accepted);
        stats.put("needsRevisionCount", needsRevision);
        
        // Quality score breakdown (averages)
        if (!scenarios.isEmpty()) {
            double avgClarity = scenarios.stream()
                .mapToDouble(s -> s.getClarityScore() != null ? s.getClarityScore() : 0.0)
                .average().orElse(0.0);
            double avgBusinessValue = scenarios.stream()
                .mapToDouble(s -> s.getBusinessValueScore() != null ? s.getBusinessValueScore() : 0.0)
                .average().orElse(0.0);
            double avgGherkin = scenarios.stream()
                .mapToDouble(s -> s.getGherkinScore() != null ? s.getGherkinScore() : 0.0)
                .average().orElse(0.0);
            double avgTestability = scenarios.stream()
                .mapToDouble(s -> s.getTestabilityScore() != null ? s.getTestabilityScore() : 0.0)
                .average().orElse(0.0);
            
            Map<String, Double> qualityBreakdown = new HashMap<>();
            qualityBreakdown.put("clarity", avgClarity);
            qualityBreakdown.put("businessValue", avgBusinessValue);
            qualityBreakdown.put("gherkin", avgGherkin);
            qualityBreakdown.put("testability", avgTestability);
            
            stats.put("qualityBreakdown", qualityBreakdown);
        }
        
        return stats;
    }
    
    /**
     * Reanalyze an existing scenario
     */
    public Scenario reanalyzeScenario(Long scenarioId) {
        Scenario scenario = getScenarioById(scenarioId);
        
        // Analyze again
        ScenarioAnalyzer.AnalysisResult analysis = 
            scenarioAnalyzer.analyze(scenario.getContent());
        
        // Update scores
        scenario.setClarityScore(analysis.getClarityScore());
        scenario.setBusinessValueScore(analysis.getBusinessValueScore());
        scenario.setGherkinScore(analysis.getGherkinScore());
        scenario.setTestabilityScore(analysis.getTestabilityScore());
        scenario.setSpecificityScore(analysis.getSpecificityScore());
        scenario.setDuplicationScore(analysis.getDuplicationScore());
        scenario.calculateOverallSqs();
        
        scenario.setFeedback(analysis.getFeedback());
        scenario.setIsAutomationReady(analysis.isAutomationReady());
        
        return scenarioRepository.save(scenario);
    }
}

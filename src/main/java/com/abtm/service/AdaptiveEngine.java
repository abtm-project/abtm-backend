package com.abtm.service;

import com.abtm.model.*;
import com.abtm.model.Module;
import com.abtm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adaptive Engine Service - Implements the adaptive algorithm from the research paper
 * 
 * Performance Score (PS) = 0.2×KS + 0.4×SQS + 0.15×CS + 0.15×AR + 0.1×TE
 * 
 * Proficiency Levels:
 * - Struggling: PS < 60%
 * - Progressing: 60% <= PS < 85%
 * - Mastering: PS >= 85%
 */
@Service
@Transactional
public class AdaptiveEngine {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private UserPerformanceRepository performanceRepository;
    
    @Autowired
    private ScenarioRepository scenarioRepository;
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    /**
     * Update user performance after completing an exercise or module
     */
    public UserPerformance updatePerformance(Long userId, Long moduleId,
                                            Double knowledgeScore,
                                            Double scenarioQualityScore,
                                            Double collaborationScore,
                                            Double automationReadiness,
                                            Double timeEfficiency) {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new RuntimeException("Module not found"));
        
        // Get or create performance record
        UserPerformance performance = performanceRepository
            .findByUserAndModule(user, module)
            .orElse(new UserPerformance());
        
        performance.setUser(user);
        performance.setModule(module);
        
        // Update component scores
        performance.setKnowledgeScore(knowledgeScore);
        performance.setScenarioQualityScore(scenarioQualityScore);
        performance.setCollaborationScore(collaborationScore);
        performance.setAutomationReadiness(automationReadiness);
        performance.setTimeEfficiency(timeEfficiency);
        
        // Calculate performance score
        performance.calculatePerformanceScore();
        
        // Identify weak areas
        List<String> weakAreas = identifyWeakAreas(performance);
        performance.setWeakAreas(String.join(",", weakAreas));
        
        // Recommend interventions
        if (performance.getProficiencyLevel() == UserPerformance.ProficiencyLevel.STRUGGLING) {
            performance.setInterventionsCount(performance.getInterventionsCount() + 1);
            
            // Recommend additional exercises
            List<Long> recommendedExercises = recommendExercises(user, module, weakAreas);
            performance.setRecommendedExercises(
                recommendedExercises.stream()
                    .map(String::valueOf)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("")
            );
        }
        
        // Update user's overall performance score
        user.setPerformanceScore(performance.getPerformanceScore());
        userRepository.save(user);
        
        return performanceRepository.save(performance);
    }
    
    /**
     * Calculate average SQS for a user in a module
     */
    public Double calculateAverageSQS(User user, Module module) {
        List<Scenario> scenarios = scenarioRepository.findByUser(user).stream()
            .filter(s -> s.getExercise().getModule().equals(module))
            .collect(Collectors.toList());
        
        if (scenarios.isEmpty()) {
            return 0.0;
        }
        
        return scenarios.stream()
            .mapToDouble(s -> s.getOverallSqs() != null ? s.getOverallSqs() : 0.0)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Identify weak areas based on component scores
     */
    private List<String> identifyWeakAreas(UserPerformance performance) {
        List<String> weakAreas = new ArrayList<>();
        double threshold = 60.0; // Below 60% is considered weak
        
        if (performance.getKnowledgeScore() < threshold) {
            weakAreas.add("knowledge");
        }
        if (performance.getScenarioQualityScore() < threshold) {
            weakAreas.add("scenario_quality");
        }
        if (performance.getCollaborationScore() < threshold) {
            weakAreas.add("collaboration");
        }
        if (performance.getAutomationReadiness() < threshold) {
            weakAreas.add("automation");
        }
        if (performance.getTimeEfficiency() < threshold) {
            weakAreas.add("time_management");
        }
        
        return weakAreas;
    }
    
    /**
     * Recommend additional exercises based on weak areas
     */
    private List<Long> recommendExercises(User user, Module module, List<String> weakAreas) {
        List<Long> recommendations = new ArrayList<>();
        
        // Get all exercises for this module
        List<Exercise> exercises = exerciseRepository
            .findByModuleAndRole(module, user.getRole());
        
        // If scenario quality is weak, recommend Foundation level exercises
        if (weakAreas.contains("scenario_quality")) {
            exercises.stream()
                .filter(e -> e.getDifficulty() == Exercise.DifficultyLevel.FOUNDATION)
                .limit(2)
                .forEach(e -> recommendations.add(e.getId()));
        }
        
        // If automation is weak, recommend Standard level exercises
        if (weakAreas.contains("automation")) {
            exercises.stream()
                .filter(e -> e.getDifficulty() == Exercise.DifficultyLevel.STANDARD)
                .limit(2)
                .forEach(e -> recommendations.add(e.getId()));
        }
        
        return recommendations;
    }
    
    /**
     * Determine next module for user based on performance
     */
    public Module determineNextModule(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get completed modules
        List<UserPerformance> completedModules = performanceRepository
            .findCompletedModulesByUser(user);
        
        if (completedModules.isEmpty()) {
            // Start with Module 1
            return moduleRepository.findByModuleNumber(1)
                .orElseThrow(() -> new RuntimeException("Module 1 not found"));
        }
        
        // Get current module number
        int currentModule = user.getCurrentModule();
        
        // Check if current module is completed with sufficient performance
        UserPerformance currentPerformance = completedModules.stream()
            .filter(p -> p.getModule().getModuleNumber() == currentModule)
            .findFirst()
            .orElse(null);
        
        if (currentPerformance != null && currentPerformance.getPerformanceScore() >= 70.0) {
            // Move to next module
            return moduleRepository.findByModuleNumber(currentModule + 1)
                .orElse(null);
        }
        
        // Stay on current module
        return moduleRepository.findByModuleNumber(currentModule)
            .orElseThrow(() -> new RuntimeException("Current module not found"));
    }
    
    /**
     * Get personalized learning path for user
     */
    public Map<String, Object> getPersonalizedPath(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> path = new HashMap<>();
        
        // Current proficiency level
        path.put("proficiencyLevel", user.getProficiencyLevel());
        path.put("overallPerformanceScore", user.getPerformanceScore());
        
        // Current module
        Module currentModule = moduleRepository.findByModuleNumber(user.getCurrentModule())
            .orElse(null);
        path.put("currentModule", currentModule);
        
        // Next module
        Module nextModule = determineNextModule(userId);
        path.put("nextModule", nextModule);
        
        // Performance history
        List<UserPerformance> performances = performanceRepository
            .findByUserOrderByModuleModuleNumber(user);
        path.put("performanceHistory", performances);
        
        // Recommended exercises
        if (currentModule != null) {
            UserPerformance currentPerformance = performanceRepository
                .findByUserAndModule(user, currentModule)
                .orElse(null);
            
            if (currentPerformance != null && 
                currentPerformance.getProficiencyLevel() == UserPerformance.ProficiencyLevel.STRUGGLING) {
                
                String[] recommendedIds = currentPerformance.getRecommendedExercises() != null ?
                    currentPerformance.getRecommendedExercises().split(",") : new String[0];
                
                path.put("recommendedExercises", recommendedIds);
                path.put("weakAreas", currentPerformance.getWeakAreas());
                path.put("interventionCount", currentPerformance.getInterventionsCount());
            }
        }
        
        // Progress percentage
        Long completedCount = performanceRepository.countCompletedModules(user);
        double progress = (completedCount / 4.0) * 100.0; // 4 total modules
        path.put("progressPercentage", progress);
        
        return path;
    }
    
    /**
     * Check if user is ready to advance to next module
     */
    public boolean isReadyToAdvance(Long userId, Long moduleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new RuntimeException("Module not found"));
        
        UserPerformance performance = performanceRepository
            .findByUserAndModule(user, module)
            .orElse(null);
        
        if (performance == null) {
            return false;
        }
        
        // Criteria for advancement:
        // 1. Performance Score >= 70%
        // 2. Module is marked as completed
        // 3. At least one accepted scenario
        
        boolean hasGoodScore = performance.getPerformanceScore() >= 70.0;
        boolean isCompleted = performance.getModuleCompleted();
        
        long acceptedScenarios = scenarioRepository.findByUser(user).stream()
            .filter(s -> s.getExercise().getModule().equals(module))
            .filter(s -> s.getStatus() == Scenario.ScenarioStatus.ACCEPTED)
            .count();
        
        boolean hasAcceptedScenario = acceptedScenarios > 0;
        
        return hasGoodScore && isCompleted && hasAcceptedScenario;
    }
}

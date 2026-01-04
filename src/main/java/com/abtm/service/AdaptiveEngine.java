package com.abtm.service;

import com.abtm.model.Exercise;
import com.abtm.model.Module;
import com.abtm.model.User;
import com.abtm.model.UserPerformance;
import com.abtm.repository.ExerciseRepository;
import com.abtm.repository.ModuleRepository;
import com.abtm.repository.UserPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdaptiveEngine {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserPerformanceRepository performanceRepository;

    /**
     * Get recommended next module for user
     */
    public Module getNextModule(User user) {
        // Get all active modules
        List<Module> allModules = moduleRepository.findByIsActiveTrueOrderByOrderIndex();
        
        if (allModules.isEmpty()) {
            return null;
        }

        // Get completed modules
        List<Module> completedModules = performanceRepository.findCompletedModulesByUser(user);

        // Find first uncompleted module
        for (Module module : allModules) {
            boolean isCompleted = completedModules.stream()
                .anyMatch(cm -> cm.getId().equals(module.getId()));
            
            if (!isCompleted) {
                return module;
            }
        }

        // All modules completed, return null
        return null;
    }

    /**
     * Get recommended exercises for user in a module
     */
    public List<Exercise> getRecommendedExercises(User user, Module module) {
        // Get all exercises for module filtered by role
        return exerciseRepository.findByModuleAndRole(module, user.getRole());
    }

    /**
     * Check if user can progress to next module
     */
    public boolean canProgressToNextModule(User user, Module currentModule) {
        // Get user's performance in this module
        List<UserPerformance> performances = performanceRepository.findByUserAndModule(user, currentModule);

        if (performances.isEmpty()) {
            return false;
        }

        // Calculate average performance
        double avgScore = performances.stream()
            .filter(p -> p.getPerformanceScore() != null)
            .mapToDouble(UserPerformance::getPerformanceScore)
            .average()
            .orElse(0.0);

        // User can progress if average score >= passing score (default 70)
        Double passingScore = currentModule.getPassingScore();
        if (passingScore == null) {
            passingScore = 70.0;
        }

        return avgScore >= passingScore;
    }

    /**
     * Get user's weak areas (exercises with low scores)
     */
    public List<Exercise> getWeakAreas(User user, Module module) {
        List<Exercise> weakExercises = new ArrayList<>();
        
        List<UserPerformance> performances = performanceRepository.findByUserAndModule(user, module);
        
        for (UserPerformance performance : performances) {
            if (performance.getPerformanceScore() != null && performance.getPerformanceScore() < 70) {
                weakExercises.add(performance.getExercise());
            }
        }
        
        return weakExercises;
    }

    /**
     * Get user's progress percentage in a module
     */
    public double getModuleProgress(User user, Module module) {
        // Get all exercises in module
        List<Exercise> allExercises = exerciseRepository.findByModule(module);
        
        if (allExercises.isEmpty()) {
            return 0.0;
        }

        // Get completed exercises (score >= 70)
        List<UserPerformance> performances = performanceRepository.findByUserAndModule(user, module);
        
        long completedCount = performances.stream()
            .filter(p -> p.getPerformanceScore() != null && p.getPerformanceScore() >= 70)
            .count();

        return (double) completedCount / allExercises.size() * 100.0;
    }
}

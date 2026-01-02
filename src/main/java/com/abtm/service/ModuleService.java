package com.abtm.service;

import com.abtm.model.Exercise;
import com.abtm.model.Module;
import com.abtm.model.User;
import com.abtm.repository.ExerciseRepository;
import com.abtm.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing learning modules and exercises
 */
@Service
@Transactional
public class ModuleService {
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    /**
     * Get all active modules
     */
    public List<Module> getAllModules() {
        return moduleRepository.findByIsActiveTrueOrderByOrderIndex();
    }
    
    /**
     * Get module by ID
     */
    public Module getModuleById(Long moduleId) {
        return moduleRepository.findById(moduleId)
            .orElseThrow(() -> new RuntimeException("Module not found with id: " + moduleId));
    }
    
    /**
     * Get module by module number
     */
    public Module getModuleByNumber(Integer moduleNumber) {
        return moduleRepository.findByModuleNumber(moduleNumber)
            .orElseThrow(() -> new RuntimeException("Module not found with number: " + moduleNumber));
    }
    
    /**
     * Get all exercises for a module
     */
    public List<Exercise> getModuleExercises(Long moduleId) {
        Module module = getModuleById(moduleId);
        return exerciseRepository.findByModuleAndIsActiveTrueOrderByOrderIndex(module);
    }
    
    /**
     * Get exercises for a module filtered by user role
     */
    public List<Exercise> getModuleExercisesForRole(Long moduleId, User.Role role) {
        Module module = getModuleById(moduleId);
        return exerciseRepository.findByModuleAndRole(module, role);
    }
    
    /**
     * Get exercise by ID
     */
    public Exercise getExerciseById(Long exerciseId) {
        return exerciseRepository.findById(exerciseId)
            .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));
    }
    
    /**
     * Get exercises by difficulty level
     */
    public List<Exercise> getExercisesByDifficulty(Long moduleId, 
                                                   Exercise.DifficultyLevel difficulty) {
        return exerciseRepository.findByModuleIdAndDifficulty(moduleId, difficulty);
    }
    
    /**
     * Create a new module
     */
    public Module createModule(String title, String description, 
                              Integer moduleNumber, Integer estimatedHours) {
        
        if (moduleRepository.existsByModuleNumber(moduleNumber)) {
            throw new RuntimeException("Module number already exists: " + moduleNumber);
        }
        
        Module module = new Module();
        module.setModuleNumber(moduleNumber);
        module.setTitle(title);
        module.setDescription(description);
        module.setEstimatedHours(estimatedHours);
        module.setPassingScore(70.0);
        module.setIsActive(true);
        module.setOrderIndex(moduleNumber);
        
        return moduleRepository.save(module);
    }
    
    /**
     * Create a new exercise
     */
    public Exercise createExercise(Long moduleId, String title, String description,
                                   String userStory, Exercise.DifficultyLevel difficulty,
                                   User.Role targetRole) {
        
        Module module = getModuleById(moduleId);
        
        Exercise exercise = new Exercise();
        exercise.setModule(module);
        exercise.setTitle(title);
        exercise.setDescription(description);
        exercise.setUserStory(userStory);
        exercise.setDifficulty(difficulty);
        exercise.setTargetRole(targetRole);
        exercise.setExpectedScenarios(1);
        exercise.setIsActive(true);
        
        // Set order index based on existing exercises
        List<Exercise> existingExercises = exerciseRepository.findByModule(module);
        exercise.setOrderIndex(existingExercises.size() + 1);
        
        return exerciseRepository.save(exercise);
    }
    
    /**
     * Update module
     */
    public Module updateModule(Long moduleId, String title, String description,
                              Integer estimatedHours, Double passingScore) {
        
        Module module = getModuleById(moduleId);
        
        if (title != null && !title.isEmpty()) {
            module.setTitle(title);
        }
        if (description != null) {
            module.setDescription(description);
        }
        if (estimatedHours != null) {
            module.setEstimatedHours(estimatedHours);
        }
        if (passingScore != null) {
            module.setPassingScore(passingScore);
        }
        
        return moduleRepository.save(module);
    }
    
    /**
     * Update exercise
     */
    public Exercise updateExercise(Long exerciseId, String title, String description,
                                   String userStory, String sampleSolution) {
        
        Exercise exercise = getExerciseById(exerciseId);
        
        if (title != null && !title.isEmpty()) {
            exercise.setTitle(title);
        }
        if (description != null) {
            exercise.setDescription(description);
        }
        if (userStory != null) {
            exercise.setUserStory(userStory);
        }
        if (sampleSolution != null) {
            exercise.setSampleSolution(sampleSolution);
        }
        
        return exerciseRepository.save(exercise);
    }
    
    /**
     * Deactivate module
     */
    public void deactivateModule(Long moduleId) {
        Module module = getModuleById(moduleId);
        module.setIsActive(false);
        moduleRepository.save(module);
    }
    
    /**
     * Activate module
     */
    public void activateModule(Long moduleId) {
        Module module = getModuleById(moduleId);
        module.setIsActive(true);
        moduleRepository.save(module);
    }
    
    /**
     * Deactivate exercise
     */
    public void deactivateExercise(Long exerciseId) {
        Exercise exercise = getExerciseById(exerciseId);
        exercise.setIsActive(false);
        exerciseRepository.save(exercise);
    }
    
    /**
     * Activate exercise
     */
    public void activateExercise(Long exerciseId) {
        Exercise exercise = getExerciseById(exerciseId);
        exercise.setIsActive(true);
        exerciseRepository.save(exercise);
    }
}

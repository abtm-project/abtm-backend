package com.abtm.service;

import com.abtm.model.Exercise;
import com.abtm.model.Module;
import com.abtm.model.User;
import com.abtm.repository.ExerciseRepository;
import com.abtm.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    // Get all modules
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    // Get active modules
    public List<Module> getActiveModules() {
        return moduleRepository.findByIsActiveTrueOrderByOrderIndex();
    }

    // Get module by ID
    public Module getModuleById(Long id) {
        return moduleRepository.findById(id).orElse(null);
    }

    // Get module by number
    public Module getModuleByNumber(Integer moduleNumber) {
        return moduleRepository.findByModuleNumber(moduleNumber);
    }

    // Get exercises for module (all active exercises)
    public List<Exercise> getModuleExercises(Long moduleId) {
        Module module = getModuleById(moduleId);
        if (module == null) {
            return List.of();
        }
        return exerciseRepository.findByModuleAndIsActiveTrueOrderByOrderIndex(module);
    }

    // Get exercises for module filtered by role
    public List<Exercise> getModuleExercisesForRole(Long moduleId, User.Role role) {
        Module module = getModuleById(moduleId);
        if (module == null) {
            return List.of();
        }
        return exerciseRepository.findByModuleAndRole(module, role);
    }

    // Get exercise by ID
    public Exercise getExerciseById(Long exerciseId) {
        return exerciseRepository.findById(exerciseId).orElse(null);
    }

    // Get exercises by difficulty
    public List<Exercise> getExercisesByDifficulty(Long moduleId, Exercise.DifficultyLevel difficulty) {
        return exerciseRepository.findByModuleIdAndDifficulty(moduleId, difficulty);
    }

    // Create new module
    public Module createModule(String title, String description, Integer estimatedHours, Integer moduleOrder) {
        Module module = new Module();
        module.setTitle(title);
        module.setDescription(description);
        module.setEstimatedHours(estimatedHours);
        module.setModuleOrder(moduleOrder);
        module.setPassingScore(70.0); // Default passing score
        module.setIsActive(true);
        
        return moduleRepository.save(module);
    }

    // Create new exercise
    public Exercise createExercise(Long moduleId, String title, String description, 
                                   String userStory, Exercise.DifficultyLevel difficulty, 
                                   User.Role targetRole) {
        Module module = getModuleById(moduleId);
        if (module == null) {
            throw new RuntimeException("Module not found");
        }

        Exercise exercise = new Exercise();
        exercise.setModule(module);
        exercise.setTitle(title);
        exercise.setDescription(description);
        exercise.setUserStory(userStory);
        exercise.setDifficulty(difficulty);
        exercise.setTargetRole(targetRole);
        exercise.setExpectedScenarios(3); // Default
        exercise.setIsActive(true);
        
        // Set exercise order
        long count = exerciseRepository.countByModule(module);
        exercise.setExerciseOrder((int) count + 1);

        return exerciseRepository.save(exercise);
    }

    // Update module
    public Module updateModule(Long id, String title, String description, 
                               Integer estimatedHours, Double passingScore) {
        Module module = getModuleById(id);
        if (module == null) {
            return null;
        }

        if (title != null) module.setTitle(title);
        if (description != null) module.setDescription(description);
        if (estimatedHours != null) module.setEstimatedHours(estimatedHours);
        if (passingScore != null) module.setPassingScore(passingScore);

        return moduleRepository.save(module);
    }

    // Update exercise
    public Exercise updateExercise(Long exerciseId, String title, String description, 
                                   String userStory, String sampleSolution) {
        Exercise exercise = getExerciseById(exerciseId);
        if (exercise == null) {
            return null;
        }

        if (title != null) exercise.setTitle(title);
        if (description != null) exercise.setDescription(description);
        if (userStory != null) exercise.setUserStory(userStory);
        if (sampleSolution != null) exercise.setSampleSolution(sampleSolution);

        return exerciseRepository.save(exercise);
    }

    // Activate module
    public Module activateModule(Long id) {
        Module module = getModuleById(id);
        if (module != null) {
            module.setIsActive(true);
            return moduleRepository.save(module);
        }
        return null;
    }

    // Deactivate module
    public Module deactivateModule(Long id) {
        Module module = getModuleById(id);
        if (module != null) {
            module.setIsActive(false);
            return moduleRepository.save(module);
        }
        return null;
    }

    // Activate exercise
    public Exercise activateExercise(Long id) {
        Exercise exercise = getExerciseById(id);
        if (exercise != null) {
            exercise.setIsActive(true);
            return exerciseRepository.save(exercise);
        }
        return null;
    }

    // Deactivate exercise
    public Exercise deactivateExercise(Long id) {
        Exercise exercise = getExerciseById(id);
        if (exercise != null) {
            exercise.setIsActive(false);
            return exerciseRepository.save(exercise);
        }
        return null;
    }

    // Delete module
    public boolean deleteModule(Long id) {
        if (moduleRepository.existsById(id)) {
            moduleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Delete exercise
    public boolean deleteExercise(Long id) {
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

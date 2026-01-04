package com.abtm.controller;

import com.abtm.model.Exercise;
import com.abtm.model.Module;
import com.abtm.model.User;
import com.abtm.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "*")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    /**
     * Get all modules
     */
    @GetMapping
    public ResponseEntity<List<Module>> getAllModules() {
        try {
            List<Module> modules = moduleService.getActiveModules();
            return ResponseEntity.ok(modules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get module by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getModuleById(@PathVariable Long id) {
        try {
            Module module = moduleService.getModuleById(id);
            if (module != null) {
                return ResponseEntity.ok(module);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get module by number
     */
    @GetMapping("/number/{moduleNumber}")
    public ResponseEntity<?> getModuleByNumber(@PathVariable Integer moduleNumber) {
        try {
            Module module = moduleService.getModuleByNumber(moduleNumber);
            if (module != null) {
                return ResponseEntity.ok(module);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get exercises for module (with optional role filter)
     */
    @GetMapping("/{id}/exercises")
    public ResponseEntity<?> getModuleExercises(
            @PathVariable Long id,
            @RequestParam(required = false) String role) {
        try {
            List<Exercise> exercises;
            if (role != null && !role.isEmpty()) {
                User.Role userRole = User.Role.valueOf(role.toUpperCase());
                exercises = moduleService.getModuleExercisesForRole(id, userRole);
            } else {
                exercises = moduleService.getModuleExercises(id);
            }
            return ResponseEntity.ok(exercises);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role: " + role);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get exercise by ID
     */
    @GetMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> getExerciseById(@PathVariable Long exerciseId) {
        try {
            Exercise exercise = moduleService.getExerciseById(exerciseId);
            if (exercise != null) {
                return ResponseEntity.ok(exercise);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Get exercises by difficulty
     */
    @GetMapping("/{id}/exercises/difficulty/{difficulty}")
    public ResponseEntity<?> getExercisesByDifficulty(
            @PathVariable Long id,
            @PathVariable String difficulty) {
        try {
            Exercise.DifficultyLevel level = Exercise.DifficultyLevel.valueOf(difficulty.toUpperCase());
            List<Exercise> exercises = moduleService.getExercisesByDifficulty(id, level);
            return ResponseEntity.ok(exercises);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid difficulty: " + difficulty);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Create new module
     */
    @PostMapping
    public ResponseEntity<?> createModule(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Integer estimatedHours,
            @RequestParam Integer moduleOrder) {
        try {
            Module module = moduleService.createModule(title, description, estimatedHours, moduleOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(module);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Create new exercise
     */
    @PostMapping("/{moduleId}/exercises")
    public ResponseEntity<?> createExercise(
            @PathVariable Long moduleId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String userStory,
            @RequestParam(required = false, defaultValue = "EASY") String difficulty,
            @RequestParam(required = false, defaultValue = "DEVELOPER") String targetRole) {
        try {
            Exercise.DifficultyLevel difficultyLevel = Exercise.DifficultyLevel.valueOf(difficulty.toUpperCase());
            User.Role role = User.Role.valueOf(targetRole.toUpperCase());

            Exercise exercise = moduleService.createExercise(
                moduleId, title, description, userStory, difficultyLevel, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(exercise);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid difficulty or role");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Update module
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateModule(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer estimatedHours,
            @RequestParam(required = false) Double passingScore) {
        try {
            Module module = moduleService.updateModule(id, title, description, estimatedHours, passingScore);
            if (module != null) {
                return ResponseEntity.ok(module);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Update exercise
     */
    @PutMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> updateExercise(
            @PathVariable Long exerciseId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String userStory,
            @RequestParam(required = false) String sampleSolution) {
        try {
            Exercise exercise = moduleService.updateExercise(
                exerciseId, title, description, userStory, sampleSolution);
            if (exercise != null) {
                return ResponseEntity.ok(exercise);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Activate module
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateModule(@PathVariable Long id) {
        try {
            Module module = moduleService.activateModule(id);
            if (module != null) {
                return ResponseEntity.ok(module);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Deactivate module
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateModule(@PathVariable Long id) {
        try {
            Module module = moduleService.deactivateModule(id);
            if (module != null) {
                return ResponseEntity.ok(module);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Delete module
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModule(@PathVariable Long id) {
        try {
            boolean deleted = moduleService.deleteModule(id);
            if (deleted) {
                return ResponseEntity.ok("Module deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    /**
     * Delete exercise
     */
    @DeleteMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> deleteExercise(@PathVariable Long exerciseId) {
        try {
            boolean deleted = moduleService.deleteExercise(exerciseId);
            if (deleted) {
                return ResponseEntity.ok("Exercise deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
}

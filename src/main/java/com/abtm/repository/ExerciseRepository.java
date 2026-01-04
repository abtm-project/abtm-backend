package com.abtm.repository;

import com.abtm.model.Exercise;
import com.abtm.model.Module;
import com.abtm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    
    // Find exercises by module
    List<Exercise> findByModule(Module module);
    
    // Find exercises by module ordered by exercise order
    List<Exercise> findByModuleOrderByExerciseOrder(Module module);
    
    // Find active exercises by module ordered by order index
    @Query("SELECT e FROM Exercise e WHERE e.module = :module AND e.isActive = true ORDER BY e.exerciseOrder")
    List<Exercise> findByModuleAndIsActiveTrueOrderByOrderIndex(@Param("module") Module module);
    
    // Find exercises by module and role
    @Query("SELECT e FROM Exercise e WHERE e.module = :module AND (e.targetRole = :role OR e.targetRole IS NULL) AND e.isActive = true ORDER BY e.exerciseOrder")
    List<Exercise> findByModuleAndRole(@Param("module") Module module, @Param("role") User.Role role);
    
    // Find exercises by module and difficulty
    @Query("SELECT e FROM Exercise e WHERE e.module.id = :moduleId AND e.difficulty = :difficulty")
    List<Exercise> findByModuleIdAndDifficulty(@Param("moduleId") Long moduleId, @Param("difficulty") Exercise.DifficultyLevel difficulty);
    
    // Find exercises by difficulty
    List<Exercise> findByDifficulty(Exercise.DifficultyLevel difficulty);
    
    // Count exercises in a module
    long countByModule(Module module);
}

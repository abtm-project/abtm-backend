package com.abtm.repository;

import com.abtm.model.Exercise;
import com.abtm.model.Module;
import com.abtm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Exercise entity
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    
    List<Exercise> findByModule(Module module);
    
    List<Exercise> findByModuleAndIsActiveTrueOrderByOrderIndex(Module module);
    
    List<Exercise> findByTargetRole(User.Role role);
    
    List<Exercise> findByDifficulty(Exercise.DifficultyLevel difficulty);
    
    @Query("SELECT e FROM Exercise e WHERE e.module = :module AND " +
           "(e.targetRole = :role OR e.targetRole IS NULL) AND e.isActive = true " +
           "ORDER BY e.orderIndex")
    List<Exercise> findByModuleAndRole(@Param("module") Module module, 
                                       @Param("role") User.Role role);
    
    @Query("SELECT e FROM Exercise e WHERE e.module.id = :moduleId AND " +
           "e.difficulty = :difficulty AND e.isActive = true")
    List<Exercise> findByModuleIdAndDifficulty(@Param("moduleId") Long moduleId,
                                                @Param("difficulty") Exercise.DifficultyLevel difficulty);
}

package com.abtm.repository;

import com.abtm.model.Exercise;
import com.abtm.model.Module;
import com.abtm.model.User;
import com.abtm.model.UserPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPerformanceRepository extends JpaRepository<UserPerformance, Long> {
    
    // Find by user
    List<UserPerformance> findByUser(User user);
    
    // Find by user and exercise
    List<UserPerformance> findByUserAndExercise(User user, Exercise exercise);
    
    // Find by user and module
    @Query("SELECT up FROM UserPerformance up WHERE up.user = :user AND up.exercise.module = :module")
    List<UserPerformance> findByUserAndModule(@Param("user") User user, @Param("module") Module module);
    
    // Find completed modules by user
    @Query("SELECT DISTINCT e.module FROM UserPerformance up JOIN up.exercise e WHERE up.user = :user AND up.performanceScore >= 70")
    List<Module> findCompletedModulesByUser(@Param("user") User user);
    
    // Find by user ordered by module
    @Query("SELECT up FROM UserPerformance up WHERE up.user = :user ORDER BY up.exercise.module.moduleOrder, up.lastAttemptDate DESC")
    List<UserPerformance> findByUserOrderByModuleModuleNumber(@Param("user") User user);
    
    // Count completed modules
    @Query("SELECT COUNT(DISTINCT e.module) FROM UserPerformance up JOIN up.exercise e WHERE up.user = :user AND up.performanceScore >= 70")
    Long countCompletedModules(@Param("user") User user);
    
    // Count completed modules by user ID
    @Query("SELECT COUNT(DISTINCT e.module) FROM UserPerformance up JOIN up.exercise e WHERE up.user.id = :userId AND up.performanceScore >= 70")
    Long countCompletedModulesByUserId(@Param("userId") Long userId);
    
    // Count exercises by user ID
    @Query("SELECT COUNT(DISTINCT up.exercise) FROM UserPerformance up WHERE up.user.id = :userId")
    Long countExercisesByUserId(@Param("userId") Long userId);
    
    // Get average score by user ID
    @Query("SELECT AVG(up.performanceScore) FROM UserPerformance up WHERE up.user.id = :userId")
    Double getAverageScoreByUserId(@Param("userId") Long userId);
    
    // Count scenarios by user ID
    @Query("SELECT COUNT(up) FROM UserPerformance up WHERE up.user.id = :userId")
    Long countScenariosByUserId(@Param("userId") Long userId);
}

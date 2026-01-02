package com.abtm.repository;

import com.abtm.model.Scenario;
import com.abtm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Scenario entity
 */
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    
    List<Scenario> findByUser(User user);
    
    List<Scenario> findByUserAndStatus(User user, Scenario.ScenarioStatus status);
    
    @Query("SELECT s FROM Scenario s WHERE s.user = :user ORDER BY s.submittedAt DESC")
    List<Scenario> findRecentByUser(@Param("user") User user);
    
    @Query("SELECT s FROM Scenario s WHERE s.user.id = :userId AND s.exercise.id = :exerciseId " +
           "ORDER BY s.submissionNumber DESC")
    List<Scenario> findByUserIdAndExerciseId(@Param("userId") Long userId, 
                                              @Param("exerciseId") Long exerciseId);
    
    @Query("SELECT AVG(s.overallSqs) FROM Scenario s WHERE s.user = :user")
    Double calculateAverageSqsForUser(@Param("user") User user);
    
    @Query("SELECT COUNT(s) FROM Scenario s WHERE s.user = :user AND s.isAutomationReady = true")
    Long countAutomationReadyScenarios(@Param("user") User user);
    
    @Query("SELECT s FROM Scenario s WHERE s.user = :user AND s.overallSqs < :threshold")
    List<Scenario> findLowQualityScenarios(@Param("user") User user, 
                                           @Param("threshold") Double threshold);
    
    @Query("SELECT s FROM Scenario s WHERE s.submittedAt BETWEEN :startDate AND :endDate")
    List<Scenario> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
}

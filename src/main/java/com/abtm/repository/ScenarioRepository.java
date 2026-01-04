package com.abtm.repository;

import com.abtm.model.Exercise;
import com.abtm.model.Scenario;
import com.abtm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    
    // Find scenarios by user
    List<Scenario> findByUser(User user);
    
    // Find scenarios by exercise
    List<Scenario> findByExercise(Exercise exercise);
    
    // Find scenarios by user and exercise
    List<Scenario> findByUserAndExercise(User user, Exercise exercise);
    
    // Find scenarios by user ID
    List<Scenario> findByUserId(Long userId);
    
    // Find scenarios by exercise ID
    List<Scenario> findByExerciseId(Long exerciseId);
    
    // Find scenarios by status
    List<Scenario> findByStatus(Scenario.ScenarioStatus status);
    
    // Find scenarios by user and status
    List<Scenario> findByUserAndStatus(User user, Scenario.ScenarioStatus status);
    
    // Count scenarios by user
    long countByUser(User user);
    
    // Count scenarios by user and status
    long countByUserAndStatus(User user, Scenario.ScenarioStatus status);
}

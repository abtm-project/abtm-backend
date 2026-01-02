package com.abtm.repository;

import com.abtm.model.Module;
import com.abtm.model.User;
import com.abtm.model.UserPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserPerformance entity
 */
@Repository
public interface UserPerformanceRepository extends JpaRepository<UserPerformance, Long> {
    
    Optional<UserPerformance> findByUserAndModule(User user, Module module);
    
    List<UserPerformance> findByUser(User user);
    
    List<UserPerformance> findByUserOrderByModuleModuleNumber(User user);
    
    @Query("SELECT up FROM UserPerformance up WHERE up.user = :user AND up.moduleCompleted = true")
    List<UserPerformance> findCompletedModulesByUser(@Param("user") User user);
    
    @Query("SELECT AVG(up.performanceScore) FROM UserPerformance up WHERE up.user = :user")
    Double calculateAveragePerformanceScore(@Param("user") User user);
    
    @Query("SELECT up FROM UserPerformance up WHERE up.user = :user AND " +
           "up.proficiencyLevel = :level")
    List<UserPerformance> findByUserAndProficiencyLevel(@Param("user") User user,
                                                         @Param("level") UserPerformance.ProficiencyLevel level);
    
    @Query("SELECT COUNT(up) FROM UserPerformance up WHERE up.user = :user AND " +
           "up.moduleCompleted = true")
    Long countCompletedModules(@Param("user") User user);
}

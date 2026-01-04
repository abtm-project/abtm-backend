package com.abtm.repository;

import com.abtm.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    // Find all active modules ordered by order
    @Query("SELECT m FROM Module m WHERE m.isActive = true ORDER BY m.moduleOrder")
    List<Module> findByIsActiveTrueOrderByOrderIndex();
    
    // Find all modules ordered by module order
    @Query("SELECT m FROM Module m ORDER BY m.moduleOrder")
    List<Module> findAllByOrderByModuleOrder();
    
    // Find module by module number
    @Query("SELECT m FROM Module m WHERE m.moduleOrder = :moduleNumber")
    Module findByModuleNumber(@Param("moduleNumber") Integer moduleNumber);
    
    // Check if module number exists
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Module m WHERE m.moduleOrder = :moduleNumber")
    boolean existsByModuleNumber(@Param("moduleNumber") Integer moduleNumber);
    
    // Find modules by difficulty
    List<Module> findByDifficulty(String difficulty);
    
    // Find module by title
    Module findByTitle(String title);
    
    // Get user's completed modules
    @Query("SELECT DISTINCT m FROM Module m JOIN Exercise e ON e.module = m JOIN UserPerformance up ON up.exercise = e WHERE up.user.id = :userId AND up.performanceScore >= 70")
    List<Module> findCompletedModulesByUserId(@Param("userId") Long userId);
}

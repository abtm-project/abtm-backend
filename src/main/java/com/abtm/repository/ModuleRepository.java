package com.abtm.repository;

import com.abtm.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Module entity
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    Optional<Module> findByModuleNumber(Integer moduleNumber);
    
    List<Module> findByIsActiveTrueOrderByOrderIndex();
    
    @Query("SELECT m FROM Module m WHERE m.isActive = true AND m.moduleNumber <= :maxModule ORDER BY m.orderIndex")
    List<Module> findAvailableModulesUpTo(Integer maxModule);
    
    boolean existsByModuleNumber(Integer moduleNumber);
}

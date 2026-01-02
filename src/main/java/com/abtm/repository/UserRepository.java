package com.abtm.repository;

import com.abtm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    List<User> findByRole(User.Role role);
    
    List<User> findByProficiencyLevel(User.ProficiencyLevel proficiencyLevel);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.performanceScore DESC")
    List<User> findTopPerformers();
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.performanceScore >= :minScore")
    List<User> findByRoleAndMinScore(@Param("role") User.Role role, 
                                      @Param("minScore") Double minScore);
}

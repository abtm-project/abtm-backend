package com.abtm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity - Represents learners in the ABTM platform
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Size(max = 100)
    private String password;
    
    @NotBlank
    @Size(max = 100)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // DEVELOPER, TESTER, PRODUCT_OWNER
    
    @Column(name = "years_experience")
    private Integer yearsExperience;
    
    @Column(name = "prior_bdd_experience")
    private Boolean priorBddExperience = false;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level")
    private ProficiencyLevel proficiencyLevel = ProficiencyLevel.BEGINNER;
    
    @Column(name = "performance_score")
    private Double performanceScore = 0.0;
    
    @Column(name = "current_module")
    private Integer currentModule = 1;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_completed_modules",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "module_id")
    )
    private Set<Module> completedModules = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // Enums
    public enum Role {
        DEVELOPER,
        TESTER,
        PRODUCT_OWNER
    }
    
    public enum ProficiencyLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }
}

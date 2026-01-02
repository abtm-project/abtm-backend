package com.abtm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Exercise Entity - Represents practice exercises in modules
 */
@Entity
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "user_story", columnDefinition = "TEXT")
    private String userStory;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty = DifficultyLevel.STANDARD;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "target_role")
    private User.Role targetRole; // null means for all roles
    
    @Column(name = "expected_scenarios")
    private Integer expectedScenarios = 1;
    
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "order_index")
    private Integer orderIndex;
    
    // Sample solution (for instructor reference)
    @Column(name = "sample_solution", columnDefinition = "TEXT")
    private String sampleSolution;
    
    public enum DifficultyLevel {
        FOUNDATION,   // Complete user story, pre-defined structure, 1 happy path
        STANDARD,     // User story provided, 2-3 scenarios, self-determined structure
        ADVANCED      // Feature description only, comprehensive suite, data tables
    }
}

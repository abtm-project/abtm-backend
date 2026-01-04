package com.abtm.model;

import lombok.Data;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "exercises")
public class Exercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Module module;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "user_story", columnDefinition = "TEXT")
    private String userStory;
    
    @Column(name = "exercise_order")
    private Integer exerciseOrder;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "target_role")
    private User.Role targetRole;
    
    @Column(name = "expected_scenarios")
    private Integer expectedScenarios;
    
    @Column(name = "sample_solution", columnDefinition = "TEXT")
    private String sampleSolution;
    
    public enum DifficultyLevel {
        EASY,
        MEDIUM,
        HARD
    }
}

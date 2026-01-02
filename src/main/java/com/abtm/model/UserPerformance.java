package com.abtm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * UserPerformance Entity - Tracks performance metrics for adaptive algorithm
 * 
 * Performance Score (PS) = 0.2×KS + 0.4×SQS + 0.15×CS + 0.15×AR + 0.1×TE
 * where:
 * - KS = Knowledge Score (quiz performance)
 * - SQS = Scenario Quality Score
 * - CS = Collaboration Score
 * - AR = Automation Readiness
 * - TE = Time Efficiency
 */
@Entity
@Table(name = "user_performance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPerformance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    
    // Individual component scores (0-100)
    @Column(name = "knowledge_score")
    private Double knowledgeScore = 0.0;
    
    @Column(name = "scenario_quality_score")
    private Double scenarioQualityScore = 0.0;
    
    @Column(name = "collaboration_score")
    private Double collaborationScore = 0.0;
    
    @Column(name = "automation_readiness")
    private Double automationReadiness = 0.0;
    
    @Column(name = "time_efficiency")
    private Double timeEfficiency = 0.0;
    
    // Calculated Performance Score (0-100)
    @Column(name = "performance_score")
    private Double performanceScore = 0.0;
    
    // Proficiency level based on PS
    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level")
    private ProficiencyLevel proficiencyLevel;
    
    // Adaptive interventions
    @Column(name = "interventions_count")
    private Integer interventionsCount = 0;
    
    @Column(name = "weak_areas", columnDefinition = "TEXT")
    private String weakAreas; // JSON array of weak dimensions
    
    @Column(name = "recommended_exercises", columnDefinition = "TEXT")
    private String recommendedExercises; // JSON array of exercise IDs
    
    // Time tracking
    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes = 0;
    
    @Column(name = "expected_time_minutes")
    private Integer expectedTimeMinutes;
    
    @Column(name = "module_completed")
    private Boolean moduleCompleted = false;
    
    @Column(name = "completion_date")
    private LocalDateTime completionDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ProficiencyLevel {
        STRUGGLING,    // PS < 60%
        PROGRESSING,   // 60% <= PS < 85%
        MASTERING      // PS >= 85%
    }
    
    /**
     * Calculate Performance Score using the formula from the paper
     */
    public void calculatePerformanceScore() {
        this.performanceScore = (0.20 * knowledgeScore) +
                               (0.40 * scenarioQualityScore) +
                               (0.15 * collaborationScore) +
                               (0.15 * automationReadiness) +
                               (0.10 * timeEfficiency);
        
        // Determine proficiency level
        if (performanceScore < 60.0) {
            this.proficiencyLevel = ProficiencyLevel.STRUGGLING;
        } else if (performanceScore < 85.0) {
            this.proficiencyLevel = ProficiencyLevel.PROGRESSING;
        } else {
            this.proficiencyLevel = ProficiencyLevel.MASTERING;
        }
    }
}

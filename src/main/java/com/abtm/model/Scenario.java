package com.abtm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Scenario Entity - Represents BDD scenarios submitted by learners
 */
@Entity
@Table(name = "scenarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scenario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
    
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "submission_number")
    private Integer submissionNumber = 1;
    
    // Quality Scores (from the 6-dimensional rubric)
    @Column(name = "clarity_score")
    private Double clarityScore;
    
    @Column(name = "business_value_score")
    private Double businessValueScore;
    
    @Column(name = "gherkin_score")
    private Double gherkinScore;
    
    @Column(name = "testability_score")
    private Double testabilityScore;
    
    @Column(name = "specificity_score")
    private Double specificityScore;
    
    @Column(name = "duplication_score")
    private Double duplicationScore;
    
    @Column(name = "overall_sqs")
    private Double overallSqs;
    
    // Feedback from automated analysis
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "detected_antipatterns", columnDefinition = "TEXT")
    private String detectedAntipatterns;
    
    @Column(name = "is_automation_ready")
    private Boolean isAutomationReady = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScenarioStatus status = ScenarioStatus.SUBMITTED;
    
    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    public enum ScenarioStatus {
        SUBMITTED,
        ANALYZED,
        REVISION_NEEDED,
        ACCEPTED,
        REJECTED
    }
    
    /**
     * Calculate overall SQS using weighted formula from paper:
     * SQS = 0.2×Clarity + 0.2×BusinessValue + 0.2×Gherkin + 
     *       0.2×Testability + 0.1×Specificity + 0.1×Duplication
     */
    public void calculateOverallSqs() {
        if (clarityScore != null && businessValueScore != null && 
            gherkinScore != null && testabilityScore != null &&
            specificityScore != null && duplicationScore != null) {
            
            this.overallSqs = (0.20 * clarityScore) +
                             (0.20 * businessValueScore) +
                             (0.20 * gherkinScore) +
                             (0.20 * testabilityScore) +
                             (0.10 * specificityScore) +
                             (0.10 * duplicationScore);
        }
    }
}

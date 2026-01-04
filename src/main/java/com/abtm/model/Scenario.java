package com.abtm.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "scenarios")
public class Scenario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Exercise exercise;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "submission_number")
    private Integer submissionNumber;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    // 6 dimension scores
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
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "detected_antipatterns", columnDefinition = "TEXT")
    private String detectedAntipatterns;
    
    @Column(name = "is_automation_ready")
    private Boolean isAutomationReady;
    
    @Enumerated(EnumType.STRING)
    private ScenarioStatus status;
    
    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ScenarioStatus.SUBMITTED;
        }
    }
    
    // Helper method to calculate overall SQS from dimension scores
    public void calculateOverallSqs() {
        if (clarityScore != null && businessValueScore != null && 
            gherkinScore != null && testabilityScore != null && 
            specificityScore != null && duplicationScore != null) {
            
            // Weighted average
            double[] weights = {0.20, 0.20, 0.20, 0.15, 0.15, 0.10};
            double[] scores = {
                clarityScore, 
                businessValueScore, 
                gherkinScore, 
                testabilityScore, 
                specificityScore, 
                duplicationScore
            };
            
            double weightedSum = 0.0;
            for (int i = 0; i < weights.length; i++) {
                weightedSum += weights[i] * scores[i];
            }
            
            this.overallSqs = Math.round(weightedSum * 100.0) / 100.0;
        }
    }
    
    public enum ScenarioStatus {
        DRAFT,
        SUBMITTED,
        PASSED,
        FAILED,
        NEEDS_IMPROVEMENT
    }
}

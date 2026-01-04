package com.abtm.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@Table(name = "user_performance")
public class UserPerformance {
    
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
    
    @Column(name = "performance_score")
    private Double performanceScore;
    
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;
    
    @Column(name = "last_attempt_date")
    private LocalDateTime lastAttemptDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastAttemptDate = LocalDateTime.now();
        if (performanceScore != null && performanceScore >= 70 && completedDate == null) {
            completedDate = LocalDateTime.now();
        }
    }
}

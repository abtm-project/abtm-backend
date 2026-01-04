package com.abtm.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "modules")
public class Module {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "module_order")
    private Integer moduleOrder;
    
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    @Column(name = "passing_score")
    private Double passingScore;
    
    private String difficulty;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
}

package com.abtm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Module Entity - Represents learning modules in ABTM
 * Module 1: BDD Fundamentals
 * Module 2: Gherkin Syntax and Patterns
 * Module 3: Role-Specific Training
 * Module 4: Practical Application
 */
@Entity
@Table(name = "modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "module_number", unique = true, nullable = false)
    private Integer moduleNumber;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    @Column(name = "passing_score")
    private Double passingScore = 70.0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "order_index")
    private Integer orderIndex;
}

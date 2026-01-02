package com.abtm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class for ABTM Backend
 * 
 * Adaptive BDD Training Model - A research-based platform for 
 * teaching Behavior-Driven Development with adaptive learning paths
 * 
 * @author Your Name
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class AbtmApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbtmApplication.class, args);
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ABTM Backend Server Started Successfully          ║");
        System.out.println("║   Adaptive BDD Training Model v1.0.0                 ║");
        System.out.println("║   Running on: http://localhost:8080                  ║");
        System.out.println("║   API Docs: http://localhost:8080/api/docs          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}

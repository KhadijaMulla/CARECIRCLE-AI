package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CareCircle AI — Main Spring Boot Application Entry Point
 * 
 * Features:
 * - Google for Developers × Hack2Skill: PromptWars × Catalyst
 * - Intelligent Elderly Care & Medication Coordination
 * - Spring Boot 3.x, Spring Data JPA, Spring Security, Spring Scheduler
 */
@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
public class CareCircleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareCircleApplication.class, args);
    }
}

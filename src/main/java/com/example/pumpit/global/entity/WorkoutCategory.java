package com.example.pumpit.global.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "workout_categories")
public class WorkoutCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<WorkoutExercise> exercises;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<WorkoutRecord> records;
}

package com.example.pumpit.global.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", nullable = false)
    private WorkoutCategory category;

    @OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY)
    private Set<WorkoutRecord> records;
}

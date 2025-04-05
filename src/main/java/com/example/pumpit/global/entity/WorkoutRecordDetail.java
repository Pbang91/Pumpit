package com.example.pumpit.global.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "workout_record_details")
public class WorkoutRecordDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Short sets;

    private Short reps;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(precision = 5, scale = 2)
    private BigDecimal distance;

    @Column(precision = 5, scale = 2)
    private BigDecimal speed;

    private Short lapTime;

    private Short holdTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id", nullable = false)
    private WorkoutRecord record;
}

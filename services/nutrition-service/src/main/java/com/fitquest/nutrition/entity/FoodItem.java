package com.fitquest.nutrition.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "food_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer caloriesPer100g;

    @Column(nullable = false)
    private Double proteinPer100g;

    @Column(nullable = false)
    private Double carbsPer100g;

    @Column(nullable = false)
    private Double fatPer100g;

    @Builder.Default
    private Integer defaultServingGrams = 100;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}

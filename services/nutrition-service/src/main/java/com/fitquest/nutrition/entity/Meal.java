package com.fitquest.nutrition.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @Column(nullable = false)
    private Instant consumedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meal_items", joinColumns = @JoinColumn(name = "meal_id"))
    @Builder.Default
    private List<MealItem> items = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Integer totalCalories = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalProteinG = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalCarbsG = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalFatG = 0.0;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}

package com.fitquest.nutrition.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "nutrition_logs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "log_date"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

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

    @Column(nullable = false)
    @Builder.Default
    private Integer mealCount = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}

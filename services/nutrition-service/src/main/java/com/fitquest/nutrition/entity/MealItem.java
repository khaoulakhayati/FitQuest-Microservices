package com.fitquest.nutrition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealItem {

    @Column(name = "food_item_id", nullable = false)
    private Long foodItemId;

    @Column(nullable = false)
    private Double quantityGrams;
}

package com.fitquest.nutrition.config;

import com.fitquest.nutrition.entity.FoodItem;
import com.fitquest.nutrition.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final FoodItemRepository foodItemRepository;

    @Bean
    CommandLineRunner seedFoodItems() {
        return args -> {
            if (foodItemRepository.count() > 0) {
                return;
            }

            foodItemRepository.saveAll(java.util.List.of(
                    food("Chicken Breast", "Generic", "Protein", 165, 31.0, 0.0, 3.6, 150),
                    food("Brown Rice", "Generic", "Grains", 112, 2.6, 23.5, 0.9, 150),
                    food("Greek Yogurt", "Fage", "Dairy", 97, 9.0, 3.6, 5.0, 170),
                    food("Banana", "Fresh", "Fruit", 89, 1.1, 22.8, 0.3, 120),
                    food("Salmon Fillet", "Atlantic", "Protein", 208, 20.0, 0.0, 13.0, 150),
                    food("Oatmeal", "Quaker", "Grains", 68, 2.4, 12.0, 1.4, 40),
                    food("Broccoli", "Fresh", "Vegetables", 34, 2.8, 6.6, 0.4, 100),
                    food("Whole Egg", "Farm", "Protein", 155, 13.0, 1.1, 11.0, 50),
                    food("Almonds", "Blue Diamond", "Nuts", 579, 21.0, 22.0, 50.0, 28),
                    food("Sweet Potato", "Fresh", "Vegetables", 86, 1.6, 20.1, 0.1, 130),
                    food("Protein Shake", "Optimum", "Supplements", 120, 24.0, 3.0, 1.5, 330),
                    food("Avocado", "Fresh", "Fruit", 160, 2.0, 8.5, 14.7, 100)
            ));

            log.info("Nutrition service seed data loaded ({} food items)", foodItemRepository.count());
        };
    }

    private FoodItem food(String name, String brand, String category,
                          int calories, double protein, double carbs, double fat, int serving) {
        return FoodItem.builder()
                .name(name)
                .brand(brand)
                .category(category)
                .caloriesPer100g(calories)
                .proteinPer100g(protein)
                .carbsPer100g(carbs)
                .fatPer100g(fat)
                .defaultServingGrams(serving)
                .build();
    }
}

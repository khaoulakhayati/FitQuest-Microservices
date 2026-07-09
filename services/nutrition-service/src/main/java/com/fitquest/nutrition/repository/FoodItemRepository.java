package com.fitquest.nutrition.repository;

import com.fitquest.nutrition.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByCategoryOrderByNameAsc(String category);
}

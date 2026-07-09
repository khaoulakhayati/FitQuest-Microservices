package com.fitquest.nutrition.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class NutritionReportDto {

    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer averageCalories;
    private Double averageProteinG;
    private Double averageCarbsG;
    private Double averageFatG;
    private Integer totalCalories;
    private Integer daysLogged;
    private List<DailyNutritionDto> dailySummaries;
}

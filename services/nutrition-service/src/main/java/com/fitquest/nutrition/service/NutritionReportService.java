package com.fitquest.nutrition.service;

import com.fitquest.nutrition.dto.DailyNutritionDto;
import com.fitquest.nutrition.dto.MealDto;
import com.fitquest.nutrition.dto.NutritionReportDto;
import com.fitquest.nutrition.entity.NutritionLog;
import com.fitquest.nutrition.repository.NutritionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionReportService {

    private final NutritionLogRepository nutritionLogRepository;
    private final MealService mealService;
    private final NutritionLogService nutritionLogService;

    @Transactional
    public DailyNutritionDto getDaily(Long userId, LocalDate date) {
        nutritionLogService.refreshDailyLog(userId, date);

        NutritionLog log = nutritionLogRepository.findByUserIdAndLogDate(userId, date)
                .orElse(emptyLog(userId, date));

        List<MealDto> meals = mealService.findMealsForDate(userId, date);

        return DailyNutritionDto.builder()
                .date(date)
                .totalCalories(log.getTotalCalories())
                .totalProteinG(log.getTotalProteinG())
                .totalCarbsG(log.getTotalCarbsG())
                .totalFatG(log.getTotalFatG())
                .mealCount(log.getMealCount())
                .meals(meals)
                .build();
    }

    @Transactional
    public NutritionReportDto getReport(Long userId, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            from = to;
        }

        List<NutritionLog> logs = nutritionLogRepository
                .findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to);

        List<DailyNutritionDto> dailySummaries = new ArrayList<>();
        int totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        int daysWithData = 0;

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            final LocalDate current = date;
            NutritionLog log = logs.stream()
                    .filter(l -> l.getLogDate().equals(current))
                    .findFirst()
                    .orElse(null);

            if (log == null) {
                nutritionLogService.refreshDailyLog(userId, current);
                log = nutritionLogRepository.findByUserIdAndLogDate(userId, current)
                        .orElse(emptyLog(userId, current));
            }

            if (log.getMealCount() > 0) {
                daysWithData++;
            }

            totalCalories += log.getTotalCalories();
            totalProtein += log.getTotalProteinG();
            totalCarbs += log.getTotalCarbsG();
            totalFat += log.getTotalFatG();

            dailySummaries.add(DailyNutritionDto.builder()
                    .date(current)
                    .totalCalories(log.getTotalCalories())
                    .totalProteinG(log.getTotalProteinG())
                    .totalCarbsG(log.getTotalCarbsG())
                    .totalFatG(log.getTotalFatG())
                    .mealCount(log.getMealCount())
                    .build());
        }

        int dayCount = dailySummaries.size();
        int divisor = Math.max(daysWithData, 1);

        return NutritionReportDto.builder()
                .fromDate(from)
                .toDate(to)
                .totalCalories(totalCalories)
                .averageCalories(totalCalories / divisor)
                .averageProteinG(Math.round(totalProtein / divisor * 10.0) / 10.0)
                .averageCarbsG(Math.round(totalCarbs / divisor * 10.0) / 10.0)
                .averageFatG(Math.round(totalFat / divisor * 10.0) / 10.0)
                .daysLogged(daysWithData)
                .dailySummaries(dailySummaries)
                .build();
    }

    private NutritionLog emptyLog(Long userId, LocalDate date) {
        return NutritionLog.builder()
                .userId(userId)
                .logDate(date)
                .totalCalories(0)
                .totalProteinG(0.0)
                .totalCarbsG(0.0)
                .totalFatG(0.0)
                .mealCount(0)
                .build();
    }
}

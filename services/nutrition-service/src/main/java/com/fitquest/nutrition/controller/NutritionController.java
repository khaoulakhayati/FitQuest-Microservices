package com.fitquest.nutrition.controller;

import com.fitquest.nutrition.dto.DailyNutritionDto;
import com.fitquest.nutrition.dto.NutritionReportDto;
import com.fitquest.nutrition.service.NutritionReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/nutrition")
@RequiredArgsConstructor
@Tag(name = "Nutrition Analytics")
public class NutritionController {

    private final NutritionReportService nutritionReportService;

    @GetMapping("/daily")
    @Operation(summary = "Get daily nutrition summary")
    public DailyNutritionDto daily(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return nutritionReportService.getDaily(userId, target);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get daily nutrition summary")
    public DailyNutritionDto summary(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return daily(userId, date);
    }

    @GetMapping("/report")
    @Operation(summary = "Get nutrition report for a date range")
    public NutritionReportDto report(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate end = to != null ? to : LocalDate.now();
        LocalDate start = from != null ? from : end.minusDays(6);
        return nutritionReportService.getReport(userId, start, end);
    }
}

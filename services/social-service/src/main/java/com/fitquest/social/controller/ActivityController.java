package com.fitquest.social.controller;

import com.fitquest.social.dto.ActivityDto;
import com.fitquest.social.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/social/activities")
@RequiredArgsConstructor
@Tag(name = "Activity Feed")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/feed")
    @Operation(summary = "Activity feed from self and accepted friends")
    public List<ActivityDto> feed(@RequestHeader("X-User-Id") Long userId) {
        return activityService.feed(userId);
    }

    @GetMapping("/me")
    @Operation(summary = "Activity history for the current user")
    public List<ActivityDto> myActivity(@RequestHeader("X-User-Id") Long userId) {
        return activityService.myActivity(userId);
    }
}

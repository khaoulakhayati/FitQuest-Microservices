package com.fitquest.social.controller;

import com.fitquest.social.dto.NotificationDto;
import com.fitquest.social.dto.NotificationPollResponse;
import com.fitquest.social.dto.UnreadCountDto;
import com.fitquest.social.service.MessageService;
import com.fitquest.social.service.NotificationService;
import com.fitquest.social.service.NotificationSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSseService sseService;
    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "List all notifications for the current user")
    public List<NotificationDto> list(@RequestHeader("X-User-Id") Long userId) {
        return notificationService.findAll(userId);
    }

    @GetMapping("/unread")
    @Operation(summary = "List unread notifications")
    public List<NotificationDto> unread(@RequestHeader("X-User-Id") Long userId) {
        return notificationService.findUnread(userId);
    }

    @GetMapping("/poll")
    @Operation(summary = "Poll for notifications created after a timestamp")
    public NotificationPollResponse poll(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "since", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
        return notificationService.poll(userId, since);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "SSE stream for realtime notifications")
    public SseEmitter stream(@RequestHeader("X-User-Id") Long userId) {
        return sseService.subscribe(userId);
    }

    @GetMapping("/unread-counts")
    @Operation(summary = "Unread counts for notifications and messages")
    public UnreadCountDto unreadCounts(@RequestHeader("X-User-Id") Long userId) {
        return new UnreadCountDto(
                notificationService.unreadCount(userId),
                messageService.unreadCount(userId)
        );
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public NotificationDto markRead(@RequestHeader("X-User-Id") Long userId, @PathVariable String id) {
        return notificationService.markRead(userId, id);
    }

    @PostMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public void markAllRead(@RequestHeader("X-User-Id") Long userId) {
        notificationService.markAllRead(userId);
    }
}

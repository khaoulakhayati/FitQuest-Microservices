package com.fitquest.social.controller;

import com.fitquest.social.dto.MessageDto;
import com.fitquest.social.dto.SendMessageRequest;
import com.fitquest.social.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/conversation/{peerId}")
    @Operation(summary = "Get conversation with a friend")
    public List<MessageDto> conversation(@RequestHeader("X-User-Id") Long userId,
                                         @PathVariable Long peerId) {
        return messageService.conversation(userId, peerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send a direct message")
    public MessageDto send(@RequestHeader("X-User-Id") Long userId,
                           @Valid @RequestBody SendMessageRequest request) {
        return messageService.send(userId, request);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a message as read")
    public MessageDto markRead(@RequestHeader("X-User-Id") Long userId, @PathVariable String id) {
        return messageService.markRead(userId, id);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Count unread messages")
    public long unreadCount(@RequestHeader("X-User-Id") Long userId) {
        return messageService.unreadCount(userId);
    }
}

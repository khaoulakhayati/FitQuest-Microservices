package com.fitquest.social.controller;

import com.fitquest.social.dto.FriendDto;
import com.fitquest.social.dto.SendFriendRequest;
import com.fitquest.social.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Tag(name = "Friends")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    @Operation(summary = "List accepted friends")
    public List<FriendDto> list(@RequestHeader("X-User-Id") Long userId) {
        return friendService.listFriends(userId);
    }

    @GetMapping("/pending")
    @Operation(summary = "List pending friend requests")
    public List<FriendDto> pending(@RequestHeader("X-User-Id") Long userId) {
        return friendService.listPending(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send a friend request")
    public FriendDto sendRequest(@RequestHeader("X-User-Id") Long userId,
                                 @Valid @RequestBody SendFriendRequest request) {
        return friendService.sendRequest(userId, request);
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Accept a pending friend request")
    public FriendDto accept(@RequestHeader("X-User-Id") Long userId, @PathVariable String id) {
        return friendService.accept(userId, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a friend or cancel a request")
    public void remove(@RequestHeader("X-User-Id") Long userId, @PathVariable String id) {
        friendService.remove(userId, id);
    }
}

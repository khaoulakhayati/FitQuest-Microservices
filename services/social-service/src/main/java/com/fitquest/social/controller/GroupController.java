package com.fitquest.social.controller;

import com.fitquest.social.dto.AddGroupMemberRequest;
import com.fitquest.social.dto.CreateGroupRequest;
import com.fitquest.social.dto.FitnessGroupDto;
import com.fitquest.social.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/social/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public List<FitnessGroupDto> myGroups(@RequestHeader("X-User-Id") Long userId) {
        return groupService.myGroups(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FitnessGroupDto create(@RequestHeader("X-User-Id") Long userId,
                                  @RequestHeader(value = "X-User-Roles", required = false) String roles,
                                  @Valid @RequestBody CreateGroupRequest request) {
        return groupService.create(userId, roles, request);
    }

    @PostMapping("/{id}/members")
    public FitnessGroupDto addMember(@RequestHeader("X-User-Id") Long userId,
                                     @RequestHeader(value = "X-User-Roles", required = false) String roles,
                                     @PathVariable String id,
                                     @Valid @RequestBody AddGroupMemberRequest request) {
        return groupService.addMember(userId, roles, id, request);
    }
}

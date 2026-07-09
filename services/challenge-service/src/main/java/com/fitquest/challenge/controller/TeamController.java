package com.fitquest.challenge.controller;

import com.fitquest.challenge.dto.CreateTeamRequest;
import com.fitquest.challenge.dto.TeamDto;
import com.fitquest.challenge.dto.UpdateTeamRequest;
import com.fitquest.challenge.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "Teams")
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    @Operation(summary = "List teams, optionally filtered by challenge")
    public List<TeamDto> list(@RequestParam(name = "challengeId", required = false) String challengeId) {
        return teamService.findAll(challengeId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by id")
    public TeamDto get(@PathVariable String id) {
        return teamService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a team")
    public TeamDto create(@Valid @RequestBody CreateTeamRequest request) {
        return teamService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a team")
    public TeamDto update(@PathVariable String id, @RequestBody UpdateTeamRequest request) {
        return teamService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a team")
    public void delete(@PathVariable String id) {
        teamService.delete(id);
    }
}

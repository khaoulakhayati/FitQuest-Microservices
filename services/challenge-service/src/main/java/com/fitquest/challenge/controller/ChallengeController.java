package com.fitquest.challenge.controller;

import com.fitquest.challenge.dto.*;
import com.fitquest.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
@Tag(name = "Challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    @Operation(summary = "List all challenges")
    public List<ChallengeDto> list(@RequestParam(name = "active", required = false) Boolean active) {
        if (Boolean.TRUE.equals(active)) {
            return challengeService.findActive();
        }
        return challengeService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get challenge by id")
    public ChallengeDto get(@PathVariable String id) {
        return challengeService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a challenge")
    public ChallengeDto create(@Valid @RequestBody CreateChallengeRequest request) {
        return challengeService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a challenge")
    public ChallengeDto update(@PathVariable String id,
                               @RequestBody UpdateChallengeRequest request) {
        return challengeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a challenge")
    public void delete(@PathVariable String id) {
        challengeService.delete(id);
    }

    @GetMapping("/{id}/participants")
    @Operation(summary = "List participants for a challenge")
    public List<ParticipantDto> participants(@PathVariable String id) {
        return challengeService.getParticipants(id);
    }

    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Join a challenge")
    public ParticipantDto join(@PathVariable String id,
                                 @Valid @RequestBody JoinChallengeRequest request) {
        return challengeService.join(id, request);
    }

    @PostMapping("/{id}/points")
    @Operation(summary = "Add points to a participant")
    public ParticipantDto addPoints(@PathVariable String id,
                                    @Valid @RequestBody AddPointsRequest request) {
        return challengeService.addPoints(id, request);
    }
}

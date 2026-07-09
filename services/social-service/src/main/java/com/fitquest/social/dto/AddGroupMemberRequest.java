package com.fitquest.social.dto;

import jakarta.validation.constraints.NotNull;

public record AddGroupMemberRequest(
        @NotNull Long userId,
        String displayName
) {
}

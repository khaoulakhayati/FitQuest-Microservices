package com.fitquest.social.dto;

import jakarta.validation.constraints.NotNull;

public record SendFriendRequest(
        @NotNull Long friendUserId,
        String friendUsername
) {
}

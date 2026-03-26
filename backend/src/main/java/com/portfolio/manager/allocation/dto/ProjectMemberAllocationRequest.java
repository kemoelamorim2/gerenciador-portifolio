package com.portfolio.manager.allocation.dto;

import jakarta.validation.constraints.NotNull;

public record ProjectMemberAllocationRequest(
    @NotNull(message = "memberId is required")
    Long memberId
) {
}

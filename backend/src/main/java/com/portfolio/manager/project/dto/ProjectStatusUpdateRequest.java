package com.portfolio.manager.project.dto;

import com.portfolio.manager.project.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record ProjectStatusUpdateRequest(
    @NotNull(message = "status is required")
    ProjectStatus status
) {
}

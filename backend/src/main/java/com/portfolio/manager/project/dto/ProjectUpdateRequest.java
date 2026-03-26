package com.portfolio.manager.project.dto;

import com.portfolio.manager.project.enums.ProjectStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectUpdateRequest(
    @NotBlank(message = "name is required")
    @Size(max = 150, message = "name must have at most 150 characters")
    String name,

    @NotNull(message = "startDate is required")
    LocalDate startDate,

    @NotNull(message = "expectedEndDate is required")
    LocalDate expectedEndDate,

    LocalDate actualEndDate,

    @DecimalMin(value = "0.01", message = "budget must be greater than zero")
    @NotNull(message = "budget is required")
    BigDecimal budget,

    @NotBlank(message = "description is required")
    @Size(max = 1000, message = "description must have at most 1000 characters")
    String description,

    @NotNull(message = "managerId is required")
    Long managerId,

    @NotNull(message = "status is required")
    ProjectStatus status
) {
}

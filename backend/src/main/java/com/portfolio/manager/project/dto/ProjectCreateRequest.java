package com.portfolio.manager.project.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectCreateRequest(
    @NotBlank(message = "name is required")
    @Size(max = 150, message = "name must have at most 150 characters")
    String name,

    @NotNull(message = "startDate is required")
    LocalDate startDate,

    @NotNull(message = "expectedEndDate is required")
    @FutureOrPresent(message = "expectedEndDate must be today or a future date")
    LocalDate expectedEndDate,

    @DecimalMin(value = "0.01", message = "budget must be greater than zero")
    @NotNull(message = "budget is required")
    BigDecimal budget,

    @NotBlank(message = "description is required")
    @Size(max = 1000, message = "description must have at most 1000 characters")
    String description,

    @NotNull(message = "managerId is required")
    Long managerId
) {
    @AssertTrue(message = "expectedEndDate must be after or equal to startDate")
    public boolean isValidDateRange() {
        if (startDate == null || expectedEndDate == null) {
            return true;
        }

        return !expectedEndDate.isBefore(startDate);
    }
}

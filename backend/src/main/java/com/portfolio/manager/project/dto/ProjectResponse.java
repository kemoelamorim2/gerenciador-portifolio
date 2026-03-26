package com.portfolio.manager.project.dto;

import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectResponse(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate expectedEndDate,
    LocalDate actualEndDate,
    BigDecimal budget,
    String description,
    Long managerId,
    String managerName,
    ProjectStatus status,
    RiskLevel riskLevel
) {
}

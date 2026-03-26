package com.portfolio.manager.report.dto;

import com.portfolio.manager.project.enums.ProjectStatus;
import java.math.BigDecimal;

public record PortfolioStatusSummaryResponse(
    ProjectStatus status,
    long projectCount,
    BigDecimal totalBudget
) {
}

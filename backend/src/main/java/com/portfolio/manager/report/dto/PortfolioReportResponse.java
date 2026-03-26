package com.portfolio.manager.report.dto;

import java.util.List;

public record PortfolioReportResponse(
    List<PortfolioStatusSummaryResponse> statusSummary,
    double averageClosedProjectDurationInDays,
    long totalUniqueAllocatedMembers
) {
}

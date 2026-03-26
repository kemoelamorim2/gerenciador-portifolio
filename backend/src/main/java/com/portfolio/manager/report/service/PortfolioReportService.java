package com.portfolio.manager.report.service;

import com.portfolio.manager.allocation.repository.ProjectMemberAllocationRepository;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.repository.ProjectRepository;
import com.portfolio.manager.report.dto.PortfolioReportResponse;
import com.portfolio.manager.report.dto.PortfolioStatusSummaryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioReportService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberAllocationRepository allocationRepository;

    public PortfolioReportService(
        ProjectRepository projectRepository,
        ProjectMemberAllocationRepository allocationRepository
    ) {
        this.projectRepository = projectRepository;
        this.allocationRepository = allocationRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioReportResponse generateReport() {
        List<Project> projects = projectRepository.findAll();

        List<PortfolioStatusSummaryResponse> statusSummary = Arrays.stream(ProjectStatus.values())
            .map(status -> buildStatusSummary(status, projects))
            .toList();

        return new PortfolioReportResponse(
            statusSummary,
            calculateAverageClosedProjectDuration(projects),
            allocationRepository.countDistinctAllocatedMembers()
        );
    }

    private PortfolioStatusSummaryResponse buildStatusSummary(ProjectStatus status, List<Project> projects) {
        List<Project> projectsByStatus = projects.stream()
            .filter(project -> project.getStatus() == status)
            .toList();

        BigDecimal totalBudget = projectsByStatus.stream()
            .map(Project::getBudget)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PortfolioStatusSummaryResponse(status, projectsByStatus.size(), totalBudget);
    }

    private double calculateAverageClosedProjectDuration(List<Project> projects) {
        List<Long> durations = projects.stream()
            .filter(project -> project.getStatus() == ProjectStatus.ENCERRADO)
            .filter(project -> project.getActualEndDate() != null)
            .map(project -> ChronoUnit.DAYS.between(project.getStartDate(), project.getActualEndDate()))
            .toList();

        if (durations.isEmpty()) {
            return 0.0;
        }

        double average = durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
        return BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

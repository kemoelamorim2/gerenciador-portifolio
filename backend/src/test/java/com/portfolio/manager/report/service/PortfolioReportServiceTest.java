package com.portfolio.manager.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.portfolio.manager.allocation.repository.ProjectMemberAllocationRepository;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import com.portfolio.manager.project.repository.ProjectRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PortfolioReportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberAllocationRepository allocationRepository;

    private PortfolioReportService portfolioReportService;

    @BeforeEach
    void setUp() {
        portfolioReportService = new PortfolioReportService(projectRepository, allocationRepository);
    }

    @Test
    void shouldGeneratePortfolioSummary() {
        when(projectRepository.findAll()).thenReturn(List.of(
            createProject(1L, ProjectStatus.EM_ANALISE, "100000.00", null),
            createProject(2L, ProjectStatus.ENCERRADO, "250000.00", LocalDate.of(2026, 5, 30)),
            createProject(3L, ProjectStatus.ENCERRADO, "300000.00", LocalDate.of(2026, 6, 9))
        ));
        when(allocationRepository.countDistinctAllocatedMembers()).thenReturn(5L);

        var report = portfolioReportService.generateReport();

        assertEquals(8, report.statusSummary().size());
        assertEquals(5L, report.totalUniqueAllocatedMembers());
        assertEquals(65.0, report.averageClosedProjectDurationInDays());
    }

    private Project createProject(Long id, ProjectStatus status, String budget, LocalDate actualEndDate) {
        Project project = new Project();
        project.setId(id);
        project.setName("Projeto " + id);
        project.setStartDate(LocalDate.of(2026, 3, 31));
        project.setExpectedEndDate(LocalDate.of(2026, 7, 31));
        project.setActualEndDate(actualEndDate);
        project.setBudget(new BigDecimal(budget));
        project.setDescription("Descricao");
        project.setManager(new Member(1L, "Gerente", "gerente"));
        project.setStatus(status);
        project.setRiskLevel(RiskLevel.MEDIO);
        return project;
    }
}

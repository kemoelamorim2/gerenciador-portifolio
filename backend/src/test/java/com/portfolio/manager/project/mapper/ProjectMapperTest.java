package com.portfolio.manager.project.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.portfolio.manager.config.ModelMapperConfig;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.project.dto.ProjectCreateRequest;
import com.portfolio.manager.project.dto.ProjectResponse;
import com.portfolio.manager.project.dto.ProjectUpdateRequest;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectMapperTest {

    private ProjectMapper projectMapper;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper(new ModelMapperConfig().modelMapper());
    }

    @Test
    void shouldMapCreateRequestToProjectEntity() {
        ProjectCreateRequest request = new ProjectCreateRequest(
            "Projeto A",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 6, 26),
            new BigDecimal("100000.00"),
            "Descricao do projeto",
            1L
        );
        Member manager = new Member(1L, "Maria", "gerente");

        Project project = projectMapper.toEntity(request, manager);

        assertEquals("Projeto A", project.getName());
        assertEquals(manager, project.getManager());
        assertEquals(new BigDecimal("100000.00"), project.getBudget());
    }

    @Test
    void shouldUpdateExistingProjectEntity() {
        Project project = new Project();
        project.setName("Projeto Antigo");

        ProjectUpdateRequest request = new ProjectUpdateRequest(
            "Projeto Atualizado",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 7, 26),
            LocalDate.of(2026, 7, 20),
            new BigDecimal("220000.00"),
            "Descricao atualizada",
            2L,
            ProjectStatus.EM_ANDAMENTO
        );
        Member manager = new Member(2L, "Joao", "gerente");

        projectMapper.updateEntity(request, project, manager);

        assertEquals("Projeto Atualizado", project.getName());
        assertEquals(ProjectStatus.EM_ANDAMENTO, project.getStatus());
        assertEquals(manager, project.getManager());
    }

    @Test
    void shouldMapProjectEntityToResponse() {
        Member manager = new Member(3L, "Ana", "gerente");
        Project project = new Project();
        project.setId(10L);
        project.setName("Projeto Response");
        project.setStartDate(LocalDate.of(2026, 3, 1));
        project.setExpectedEndDate(LocalDate.of(2026, 8, 1));
        project.setBudget(new BigDecimal("500000.00"));
        project.setDescription("Projeto para teste de response");
        project.setManager(manager);
        project.setStatus(ProjectStatus.ANALISE_APROVADA);
        project.setRiskLevel(RiskLevel.MEDIO);

        ProjectResponse response = projectMapper.toResponse(project);

        assertEquals(10L, response.id());
        assertEquals(3L, response.managerId());
        assertEquals("Ana", response.managerName());
        assertEquals(RiskLevel.MEDIO, response.riskLevel());
    }
}

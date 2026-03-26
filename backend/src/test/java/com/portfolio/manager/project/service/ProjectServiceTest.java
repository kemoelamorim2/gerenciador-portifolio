package com.portfolio.manager.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.manager.config.ModelMapperConfig;
import com.portfolio.manager.exception.InvalidStatusTransitionException;
import com.portfolio.manager.exception.ProjectDeletionNotAllowedException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import com.portfolio.manager.project.dto.ProjectCreateRequest;
import com.portfolio.manager.project.dto.ProjectFilterRequest;
import com.portfolio.manager.project.dto.ProjectStatusUpdateRequest;
import com.portfolio.manager.project.dto.ProjectUpdateRequest;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import com.portfolio.manager.project.mapper.ProjectMapper;
import com.portfolio.manager.project.repository.ProjectRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    private ProjectMapper projectMapper;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper(new ModelMapperConfig().modelMapper());
        projectService = new ProjectService(projectRepository, memberRepository, projectMapper);
    }

    @Test
    void shouldCreateProjectWithInitialStatusAndLowRisk() {
        Member manager = createManager(1L);
        ProjectCreateRequest request = new ProjectCreateRequest(
            "Projeto A",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 6, 26),
            new BigDecimal("100000.00"),
            "Descricao",
            1L
        );

        when(memberRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project project = invocation.getArgument(0);
            project.setId(10L);
            return project;
        });

        var response = projectService.create(request);

        assertEquals(10L, response.id());
        assertEquals(ProjectStatus.EM_ANALISE, response.status());
        assertEquals(RiskLevel.BAIXO, response.riskLevel());
    }

    @Test
    void shouldCalculateHighRiskForBudgetAboveThreshold() {
        Member manager = createManager(1L);
        ProjectCreateRequest request = new ProjectCreateRequest(
            "Projeto Alto Risco",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 12, 27),
            new BigDecimal("600000.00"),
            "Descricao",
            1L
        );

        when(memberRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = projectService.create(request);

        assertEquals(RiskLevel.ALTO, response.riskLevel());
    }

    @Test
    void shouldReturnAllProjects() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        when(projectRepository.findAll()).thenReturn(List.of(project));

        var response = projectService.findAllWithoutPaging();

        assertEquals(1, response.size());
        assertEquals("Projeto Teste", response.get(0).name());
    }

    @Test
    void shouldReturnPagedProjectsWithFilters() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(project)));

        var response = projectService.findAll(new ProjectFilterRequest(), PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals("Projeto Teste", response.getContent().get(0).name());
    }

    @Test
    void shouldThrowWhenProjectIsNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.findById(99L));
    }

    @Test
    void shouldUpdateProjectWhenStatusTransitionIsValid() {
        Member manager = createManager(2L);
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        ProjectUpdateRequest request = new ProjectUpdateRequest(
            "Projeto Atualizado",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 8, 26),
            null,
            new BigDecimal("250000.00"),
            "Descricao nova",
            2L,
            ProjectStatus.ANALISE_REALIZADA
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = projectService.update(1L, request);

        assertEquals(ProjectStatus.ANALISE_REALIZADA, response.status());
        assertEquals(RiskLevel.MEDIO, response.riskLevel());
        assertEquals("Projeto Atualizado", response.name());
    }

    @Test
    void shouldRejectInvalidStatusTransitionOnUpdate() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        ProjectUpdateRequest request = new ProjectUpdateRequest(
            "Projeto Atualizado",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 8, 26),
            null,
            new BigDecimal("250000.00"),
            "Descricao nova",
            1L,
            ProjectStatus.EM_ANDAMENTO
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(createManager(1L)));

        assertThrows(InvalidStatusTransitionException.class, () -> projectService.update(1L, request));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void shouldUpdateStatusWhenTransitionIsValid() {
        Project project = createProject(1L, ProjectStatus.ANALISE_REALIZADA);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = projectService.updateStatus(
            1L,
            new ProjectStatusUpdateRequest(ProjectStatus.ANALISE_APROVADA)
        );

        assertEquals(ProjectStatus.ANALISE_APROVADA, response.status());
    }

    @Test
    void shouldDeleteProjectWhenStatusAllowsDeletion() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.delete(1L);

        verify(projectRepository).delete(project);
    }

    @Test
    void shouldBlockDeletionForStartedProjects() {
        Project project = createProject(1L, ProjectStatus.INICIADO);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(ProjectDeletionNotAllowedException.class, () -> projectService.delete(1L));
        verify(projectRepository, never()).delete(any(Project.class));
    }

    private Member createManager(Long id) {
        return new Member(id, "Gerente", "gerente");
    }

    private Project createProject(Long id, ProjectStatus status) {
        Project project = new Project();
        project.setId(id);
        project.setName("Projeto Teste");
        project.setStartDate(LocalDate.of(2026, 3, 26));
        project.setExpectedEndDate(LocalDate.of(2026, 6, 26));
        project.setBudget(new BigDecimal("100000.00"));
        project.setDescription("Descricao");
        project.setManager(createManager(1L));
        project.setStatus(status);
        project.setRiskLevel(RiskLevel.BAIXO);
        return project;
    }
}

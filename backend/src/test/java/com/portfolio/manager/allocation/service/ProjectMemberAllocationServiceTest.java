package com.portfolio.manager.allocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.portfolio.manager.allocation.dto.ProjectMemberAllocationRequest;
import com.portfolio.manager.allocation.entity.ProjectMemberAllocation;
import com.portfolio.manager.allocation.repository.ProjectMemberAllocationRepository;
import com.portfolio.manager.exception.MemberAllocationException;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import com.portfolio.manager.project.repository.ProjectRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectMemberAllocationServiceTest {

    @Mock
    private ProjectMemberAllocationRepository allocationRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    private ProjectMemberAllocationService allocationService;

    @BeforeEach
    void setUp() {
        allocationService = new ProjectMemberAllocationService(
            allocationRepository,
            projectRepository,
            memberRepository
        );
    }

    @Test
    void shouldAllocateEmployeeMemberToProject() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        Member member = new Member(2L, "Carlos", "funcionario");
        ProjectMemberAllocationRequest request = new ProjectMemberAllocationRequest(2L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(allocationRepository.countByProjectId(1L)).thenReturn(0L);
        when(allocationRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);
        when(allocationRepository.countDistinctActiveProjectsByMemberId(2L, EnumSet.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO)))
            .thenReturn(1L);
        when(allocationRepository.save(any(ProjectMemberAllocation.class))).thenAnswer(invocation -> {
            ProjectMemberAllocation allocation = invocation.getArgument(0);
            allocation.setId(10L);
            return allocation;
        });

        var response = allocationService.allocate(1L, request);

        assertEquals(10L, response.allocationId());
        assertEquals(2L, response.memberId());
    }

    @Test
    void shouldRejectAllocationForNonEmployee() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        Member member = new Member(2L, "Carlos", "gerente");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));

        assertThrows(
            MemberAllocationException.class,
            () -> allocationService.allocate(1L, new ProjectMemberAllocationRequest(2L))
        );
        verify(allocationRepository, never()).save(any(ProjectMemberAllocation.class));
    }

    @Test
    void shouldRejectAllocationWhenProjectIsFull() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        Member member = new Member(2L, "Carlos", "funcionario");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(allocationRepository.countByProjectId(1L)).thenReturn(10L);

        assertThrows(
            MemberAllocationException.class,
            () -> allocationService.allocate(1L, new ProjectMemberAllocationRequest(2L))
        );
    }

    @Test
    void shouldRejectAllocationWhenMemberHasTooManyActiveProjects() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        Member member = new Member(2L, "Carlos", "funcionario");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(allocationRepository.countByProjectId(1L)).thenReturn(1L);
        when(allocationRepository.existsByProjectIdAndMemberId(1L, 2L)).thenReturn(false);
        when(allocationRepository.countDistinctActiveProjectsByMemberId(2L, EnumSet.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO)))
            .thenReturn(3L);

        assertThrows(
            MemberAllocationException.class,
            () -> allocationService.allocate(1L, new ProjectMemberAllocationRequest(2L))
        );
    }

    @Test
    void shouldListAllocationsByProject() {
        Project project = createProject(1L, ProjectStatus.EM_ANALISE);
        Member member = new Member(2L, "Carlos", "funcionario");
        ProjectMemberAllocation allocation = new ProjectMemberAllocation(10L, project, member);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(allocationRepository.findAllByProjectId(1L)).thenReturn(List.of(allocation));

        var response = allocationService.findAllByProjectId(1L);

        assertEquals(1, response.size());
        assertEquals("Carlos", response.get(0).memberName());
    }

    @Test
    void shouldRemoveAllocationWhenProjectKeepsMinimumMembers() {
        ProjectMemberAllocation allocation = new ProjectMemberAllocation(10L, createProject(1L, ProjectStatus.EM_ANALISE), new Member(2L, "Carlos", "funcionario"));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(createProject(1L, ProjectStatus.EM_ANALISE)));
        when(allocationRepository.findByProjectIdAndMemberId(1L, 2L)).thenReturn(Optional.of(allocation));
        when(allocationRepository.countByProjectId(1L)).thenReturn(2L);

        allocationService.remove(1L, 2L);

        verify(allocationRepository).delete(allocation);
    }

    @Test
    void shouldRejectRemovalWhenProjectWouldHaveNoMembers() {
        ProjectMemberAllocation allocation = new ProjectMemberAllocation(10L, createProject(1L, ProjectStatus.EM_ANALISE), new Member(2L, "Carlos", "funcionario"));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(createProject(1L, ProjectStatus.EM_ANALISE)));
        when(allocationRepository.findByProjectIdAndMemberId(1L, 2L)).thenReturn(Optional.of(allocation));
        when(allocationRepository.countByProjectId(1L)).thenReturn(1L);

        assertThrows(MemberAllocationException.class, () -> allocationService.remove(1L, 2L));
        verify(allocationRepository, never()).delete(any(ProjectMemberAllocation.class));
    }

    private Project createProject(Long id, ProjectStatus status) {
        Project project = new Project();
        project.setId(id);
        project.setName("Projeto");
        project.setStartDate(LocalDate.of(2026, 3, 26));
        project.setExpectedEndDate(LocalDate.of(2026, 8, 26));
        project.setBudget(new BigDecimal("100000.00"));
        project.setDescription("Descricao");
        project.setManager(new Member(1L, "Gerente", "gerente"));
        project.setStatus(status);
        project.setRiskLevel(RiskLevel.MEDIO);
        return project;
    }
}

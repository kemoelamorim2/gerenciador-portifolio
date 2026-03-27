package com.portfolio.manager.project.service;

import com.portfolio.manager.allocation.repository.ProjectMemberAllocationRepository;
import com.portfolio.manager.exception.InvalidStatusTransitionException;
import com.portfolio.manager.exception.MemberAllocationException;
import com.portfolio.manager.exception.ProjectDeletionNotAllowedException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.member.client.MemberClient;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.project.dto.ProjectCreateRequest;
import com.portfolio.manager.project.dto.ProjectFilterRequest;
import com.portfolio.manager.project.dto.PagedResponse;
import com.portfolio.manager.project.dto.ProjectResponse;
import com.portfolio.manager.project.dto.ProjectStatusUpdateRequest;
import com.portfolio.manager.project.dto.ProjectUpdateRequest;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import com.portfolio.manager.project.mapper.ProjectMapper;
import com.portfolio.manager.project.repository.ProjectRepository;
import com.portfolio.manager.project.specification.ProjectSpecification;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberClient memberClient;
    private final ProjectMemberAllocationRepository allocationRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(
        ProjectRepository projectRepository,
        MemberClient memberClient,
        ProjectMemberAllocationRepository allocationRepository,
        ProjectMapper projectMapper
    ) {
        this.projectRepository = projectRepository;
        this.memberClient = memberClient;
        this.allocationRepository = allocationRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        Member manager = findManagerById(request.managerId());
        ProjectStatus initialStatus = request.status() != null ? request.status() : ProjectStatus.EM_ANALISE;

        validateInitialStatus(initialStatus);

        Project project = projectMapper.toEntity(request, manager);
        project.setStatus(initialStatus);
        project.setRiskLevel(calculateRiskLevel(request.startDate(), request.expectedEndDate(), request.budget()));

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProjectResponse> findAll(ProjectFilterRequest filter, Pageable pageable) {
        Page<Project> page = projectRepository.findAll(ProjectSpecification.withFilters(filter), pageable);

        return new PagedResponse<>(
            page.getContent().stream().map(projectMapper::toResponse).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            page.isEmpty()
        );
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAllWithoutPaging() {
        return projectRepository.findAll().stream().map(projectMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return projectMapper.toResponse(findProjectEntityById(id));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectUpdateRequest request) {
        Project project = findProjectEntityById(id);
        Member manager = findManagerById(request.managerId());

        validateStatusTransition(project.getStatus(), request.status());
        validateMinimumMembersForOperationalStatuses(id, request.status());

        projectMapper.updateEntity(request, project, manager);
        project.setRiskLevel(calculateRiskLevel(request.startDate(), request.expectedEndDate(), request.budget()));

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    @Transactional
    public ProjectResponse updateStatus(Long id, ProjectStatusUpdateRequest request) {
        Project project = findProjectEntityById(id);

        validateStatusTransition(project.getStatus(), request.status());
        validateMinimumMembersForOperationalStatuses(id, request.status());

        project.setStatus(request.status());
        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }

    @Transactional
    public void delete(Long id) {
        Project project = findProjectEntityById(id);

        if (cannotBeDeleted(project.getStatus())) {
            throw new ProjectDeletionNotAllowedException(
                "Projects with status INICIADO, EM_ANDAMENTO or ENCERRADO cannot be deleted"
            );
        }

        allocationRepository.deleteAllByProjectId(id);
        projectRepository.delete(project);
    }

    RiskLevel calculateRiskLevel(LocalDate startDate, LocalDate expectedEndDate, BigDecimal budget) {
        int durationInMonths = calculateDurationInMonths(startDate, expectedEndDate);

        if (budget.compareTo(new BigDecimal("500000")) > 0 || durationInMonths > 6) {
            return RiskLevel.ALTO;
        }

        if (budget.compareTo(new BigDecimal("100000")) <= 0 && durationInMonths <= 3) {
            return RiskLevel.BAIXO;
        }

        return RiskLevel.MEDIO;
    }

    private int calculateDurationInMonths(LocalDate startDate, LocalDate expectedEndDate) {
        Period period = Period.between(startDate, expectedEndDate);
        int months = period.getYears() * 12 + period.getMonths();

        if (period.getDays() > 0) {
            months++;
        }

        return Math.max(months, 0);
    }

    private void validateStatusTransition(ProjectStatus currentStatus, ProjectStatus targetStatus) {
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new InvalidStatusTransitionException(
                "Invalid project status transition from " + currentStatus + " to " + targetStatus
            );
        }
    }

    private void validateInitialStatus(ProjectStatus initialStatus) {
        if (initialStatus != ProjectStatus.EM_ANALISE && initialStatus != ProjectStatus.CANCELADO) {
            throw new InvalidStatusTransitionException(
                "A project can only be created with status EM_ANALISE or CANCELADO"
            );
        }
    }

    private boolean cannotBeDeleted(ProjectStatus status) {
        return status == ProjectStatus.INICIADO
            || status == ProjectStatus.EM_ANDAMENTO
            || status == ProjectStatus.ENCERRADO;
    }

    private void validateMinimumMembersForOperationalStatuses(Long projectId, ProjectStatus targetStatus) {
        if (
            targetStatus == ProjectStatus.INICIADO
                || targetStatus == ProjectStatus.PLANEJADO
                || targetStatus == ProjectStatus.EM_ANDAMENTO
                || targetStatus == ProjectStatus.ENCERRADO
        ) {
            boolean hasMembers = allocationRepository.existsByProjectId(projectId);

            if (!hasMembers) {
                throw new MemberAllocationException(
                    "Projects must have at least 1 allocated member before moving to operational statuses"
                );
            }
        }
    }

    private Project findProjectEntityById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private Member findManagerById(Long managerId) {
        try {
            return memberClient.findById(managerId);
        } catch (ResourceNotFoundException exception) {
            throw new ResourceNotFoundException("Manager not found with id: " + managerId);
        }
    }
}

package com.portfolio.manager.project.service;

import com.portfolio.manager.exception.InvalidStatusTransitionException;
import com.portfolio.manager.exception.ProjectDeletionNotAllowedException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import com.portfolio.manager.project.dto.ProjectCreateRequest;
import com.portfolio.manager.project.dto.ProjectResponse;
import com.portfolio.manager.project.dto.ProjectStatusUpdateRequest;
import com.portfolio.manager.project.dto.ProjectUpdateRequest;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import com.portfolio.manager.project.mapper.ProjectMapper;
import com.portfolio.manager.project.repository.ProjectRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(
        ProjectRepository projectRepository,
        MemberRepository memberRepository,
        ProjectMapper projectMapper
    ) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        Member manager = findManagerById(request.managerId());

        Project project = projectMapper.toEntity(request, manager);
        project.setStatus(ProjectStatus.EM_ANALISE);
        project.setRiskLevel(calculateRiskLevel(request.startDate(), request.expectedEndDate(), request.budget()));

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll()
            .stream()
            .map(projectMapper::toResponse)
            .toList();
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

        projectMapper.updateEntity(request, project, manager);
        project.setRiskLevel(calculateRiskLevel(request.startDate(), request.expectedEndDate(), request.budget()));

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    @Transactional
    public ProjectResponse updateStatus(Long id, ProjectStatusUpdateRequest request) {
        Project project = findProjectEntityById(id);

        validateStatusTransition(project.getStatus(), request.status());

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

    private boolean cannotBeDeleted(ProjectStatus status) {
        return status == ProjectStatus.INICIADO
            || status == ProjectStatus.EM_ANDAMENTO
            || status == ProjectStatus.ENCERRADO;
    }

    private Project findProjectEntityById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private Member findManagerById(Long managerId) {
        return memberRepository.findById(managerId)
            .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + managerId));
    }
}

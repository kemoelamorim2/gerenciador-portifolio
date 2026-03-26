package com.portfolio.manager.allocation.service;

import com.portfolio.manager.allocation.dto.ProjectMemberAllocationRequest;
import com.portfolio.manager.allocation.dto.ProjectMemberAllocationResponse;
import com.portfolio.manager.allocation.entity.ProjectMemberAllocation;
import com.portfolio.manager.allocation.repository.ProjectMemberAllocationRepository;
import com.portfolio.manager.exception.MemberAllocationException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.member.repository.MemberRepository;
import com.portfolio.manager.project.entity.Project;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.repository.ProjectRepository;
import java.util.EnumSet;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectMemberAllocationService {

    private static final int MAX_MEMBERS_PER_PROJECT = 10;
    private static final int MIN_MEMBERS_PER_PROJECT = 1;
    private static final int MAX_ACTIVE_PROJECTS_PER_MEMBER = 3;
    private static final String EMPLOYEE_ASSIGNMENT = "funcionario";

    private final ProjectMemberAllocationRepository allocationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    public ProjectMemberAllocationService(
        ProjectMemberAllocationRepository allocationRepository,
        ProjectRepository projectRepository,
        MemberRepository memberRepository
    ) {
        this.allocationRepository = allocationRepository;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ProjectMemberAllocationResponse allocate(Long projectId, ProjectMemberAllocationRequest request) {
        Project project = findProjectById(projectId);
        Member member = findMemberById(request.memberId());

        validateMemberAssignment(member);
        validateProjectCapacity(projectId);
        validateDuplicateAllocation(projectId, member.getId());
        validateMemberActiveProjectsLimit(member.getId());

        ProjectMemberAllocation allocation = new ProjectMemberAllocation();
        allocation.setProject(project);
        allocation.setMember(member);

        ProjectMemberAllocation savedAllocation = allocationRepository.save(allocation);
        return toResponse(savedAllocation);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberAllocationResponse> findAllByProjectId(Long projectId) {
        findProjectById(projectId);

        return allocationRepository.findAllByProjectId(projectId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public void remove(Long projectId, Long memberId) {
        findProjectById(projectId);
        ProjectMemberAllocation allocation = allocationRepository.findByProjectIdAndMemberId(projectId, memberId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Allocation not found for project id " + projectId + " and member id " + memberId
            ));

        long currentAllocations = allocationRepository.countByProjectId(projectId);
        if (currentAllocations <= MIN_MEMBERS_PER_PROJECT) {
            throw new MemberAllocationException("A project must keep at least 1 allocated member");
        }

        allocationRepository.delete(allocation);
    }

    private void validateMemberAssignment(Member member) {
        if (!EMPLOYEE_ASSIGNMENT.equalsIgnoreCase(member.getAssignment())) {
            throw new MemberAllocationException("Only members with assignment 'funcionario' can be allocated");
        }
    }

    private void validateProjectCapacity(Long projectId) {
        long currentAllocations = allocationRepository.countByProjectId(projectId);
        if (currentAllocations >= MAX_MEMBERS_PER_PROJECT) {
            throw new MemberAllocationException("A project can have at most 10 allocated members");
        }
    }

    private void validateDuplicateAllocation(Long projectId, Long memberId) {
        if (allocationRepository.existsByProjectIdAndMemberId(projectId, memberId)) {
            throw new MemberAllocationException("Member is already allocated to this project");
        }
    }

    private void validateMemberActiveProjectsLimit(Long memberId) {
        long activeProjects = allocationRepository.countDistinctActiveProjectsByMemberId(
            memberId,
            EnumSet.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO)
        );

        if (activeProjects >= MAX_ACTIVE_PROJECTS_PER_MEMBER) {
            throw new MemberAllocationException(
                "A member cannot be allocated to more than 3 active projects"
            );
        }
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
    }

    private ProjectMemberAllocationResponse toResponse(ProjectMemberAllocation allocation) {
        return new ProjectMemberAllocationResponse(
            allocation.getId(),
            allocation.getProject().getId(),
            allocation.getMember().getId(),
            allocation.getMember().getName(),
            allocation.getMember().getAssignment()
        );
    }
}

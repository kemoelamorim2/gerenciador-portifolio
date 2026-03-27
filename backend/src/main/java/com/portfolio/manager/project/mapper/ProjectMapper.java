package com.portfolio.manager.project.mapper;

import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.project.dto.ProjectCreateRequest;
import com.portfolio.manager.project.dto.ProjectResponse;
import com.portfolio.manager.project.dto.ProjectUpdateRequest;
import com.portfolio.manager.project.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toEntity(ProjectCreateRequest request, Member manager) {
        Project project = new Project();
        project.setName(request.name());
        project.setStartDate(request.startDate());
        project.setExpectedEndDate(request.expectedEndDate());
        project.setBudget(request.budget());
        project.setDescription(request.description());
        project.setManager(manager);
        return project;
    }

    public void updateEntity(ProjectUpdateRequest request, Project project, Member manager) {
        project.setName(request.name());
        project.setStartDate(request.startDate());
        project.setExpectedEndDate(request.expectedEndDate());
        project.setActualEndDate(request.actualEndDate());
        project.setBudget(request.budget());
        project.setDescription(request.description());
        project.setManager(manager);
        project.setStatus(request.status());
    }

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getStartDate(),
            project.getExpectedEndDate(),
            project.getActualEndDate(),
            project.getBudget(),
            project.getDescription(),
            project.getManager() != null ? project.getManager().getId() : null,
            project.getManager() != null ? project.getManager().getName() : null,
            project.getStatus(),
            project.getRiskLevel()
        );
    }
}

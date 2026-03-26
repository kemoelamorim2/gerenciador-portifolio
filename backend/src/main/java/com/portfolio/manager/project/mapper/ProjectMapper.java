package com.portfolio.manager.project.mapper;

import com.portfolio.manager.member.entity.Member;
import com.portfolio.manager.project.dto.ProjectCreateRequest;
import com.portfolio.manager.project.dto.ProjectResponse;
import com.portfolio.manager.project.dto.ProjectUpdateRequest;
import com.portfolio.manager.project.entity.Project;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    private final ModelMapper modelMapper;

    public ProjectMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Project toEntity(ProjectCreateRequest request, Member manager) {
        Project project = modelMapper.map(request, Project.class);
        project.setManager(manager);
        return project;
    }

    public void updateEntity(ProjectUpdateRequest request, Project project, Member manager) {
        modelMapper.map(request, project);
        project.setManager(manager);
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

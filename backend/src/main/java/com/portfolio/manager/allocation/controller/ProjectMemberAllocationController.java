package com.portfolio.manager.allocation.controller;

import com.portfolio.manager.allocation.dto.ProjectMemberAllocationRequest;
import com.portfolio.manager.allocation.dto.ProjectMemberAllocationResponse;
import com.portfolio.manager.allocation.service.ProjectMemberAllocationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberAllocationController {

    private final ProjectMemberAllocationService allocationService;

    public ProjectMemberAllocationController(ProjectMemberAllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping
    public ResponseEntity<ProjectMemberAllocationResponse> allocate(
        @PathVariable Long projectId,
        @Valid @RequestBody ProjectMemberAllocationRequest request
    ) {
        return ResponseEntity.ok(allocationService.allocate(projectId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberAllocationResponse>> findAll(@PathVariable Long projectId) {
        return ResponseEntity.ok(allocationService.findAllByProjectId(projectId));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> remove(@PathVariable Long projectId, @PathVariable Long memberId) {
        allocationService.remove(projectId, memberId);
        return ResponseEntity.noContent().build();
    }
}

package com.portfolio.manager.project.controller;

import com.portfolio.manager.exception.ApiErrorResponse;
import com.portfolio.manager.project.dto.ProjectCreateRequest;
import com.portfolio.manager.project.dto.ProjectFilterRequest;
import com.portfolio.manager.project.dto.PagedResponse;
import com.portfolio.manager.project.dto.ProjectResponse;
import com.portfolio.manager.project.dto.ProjectStatusUpdateRequest;
import com.portfolio.manager.project.dto.ProjectUpdateRequest;
import com.portfolio.manager.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @Operation(summary = "Cria um novo projeto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
        @ApiResponse(
            responseCode = "400",
            description = "Dados invalidos",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse response = projectService.create(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista projetos com paginacao e filtros")
    public ResponseEntity<PagedResponse<ProjectResponse>> findAll(
        @ParameterObject @ModelAttribute ProjectFilterRequest filter,
        @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(projectService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um projeto por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
        @ApiResponse(
            responseCode = "404",
            description = "Projeto nao encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    public ResponseEntity<ProjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um projeto existente")
    public ResponseEntity<ProjectResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody ProjectUpdateRequest request
    ) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza o status de um projeto")
    public ResponseEntity<ProjectResponse> updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody ProjectStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(projectService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um projeto quando permitido pelas regras de negocio")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

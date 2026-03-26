package com.portfolio.manager.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.manager.config.SecurityConfig;
import com.portfolio.manager.project.dto.ProjectCreateRequest;
import com.portfolio.manager.project.dto.ProjectResponse;
import com.portfolio.manager.project.dto.ProjectStatusUpdateRequest;
import com.portfolio.manager.project.dto.ProjectUpdateRequest;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.project.enums.RiskLevel;
import com.portfolio.manager.project.service.ProjectService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateProject() throws Exception {
        ProjectCreateRequest request = new ProjectCreateRequest(
            "Projeto A",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 6, 26),
            new BigDecimal("100000.00"),
            "Descricao",
            1L
        );
        ProjectResponse response = buildResponse(ProjectStatus.EM_ANALISE, RiskLevel.BAIXO);

        when(projectService.create(any(ProjectCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/projects")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/api/projects/1"))
            .andExpect(jsonPath("$.name").value("Projeto A"))
            .andExpect(jsonPath("$.status").value("EM_ANALISE"));
    }

    @Test
    void shouldListProjects() throws Exception {
        when(projectService.findAll(any(), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(buildResponse(ProjectStatus.EM_ANALISE, RiskLevel.BAIXO))));

        mockMvc.perform(get("/api/projects?page=0&size=10").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void shouldFindProjectById() throws Exception {
        when(projectService.findById(1L)).thenReturn(buildResponse(ProjectStatus.EM_ANALISE, RiskLevel.BAIXO));

        mockMvc.perform(get("/api/projects/1").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldUpdateProject() throws Exception {
        ProjectUpdateRequest request = new ProjectUpdateRequest(
            "Projeto Atualizado",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 7, 26),
            null,
            new BigDecimal("200000.00"),
            "Descricao nova",
            1L,
            ProjectStatus.ANALISE_REALIZADA
        );

        when(projectService.update(eq(1L), any(ProjectUpdateRequest.class)))
            .thenReturn(buildResponse(ProjectStatus.ANALISE_REALIZADA, RiskLevel.MEDIO));

        mockMvc.perform(put("/api/projects/1")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ANALISE_REALIZADA"));
    }

    @Test
    void shouldUpdateProjectStatus() throws Exception {
        ProjectStatusUpdateRequest request = new ProjectStatusUpdateRequest(ProjectStatus.ANALISE_APROVADA);

        when(projectService.updateStatus(eq(1L), any(ProjectStatusUpdateRequest.class)))
            .thenReturn(buildResponse(ProjectStatus.ANALISE_APROVADA, RiskLevel.MEDIO));

        mockMvc.perform(patch("/api/projects/1/status")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ANALISE_APROVADA"));
    }

    @Test
    void shouldDeleteProject() throws Exception {
        doNothing().when(projectService).delete(1L);

        mockMvc.perform(delete("/api/projects/1").with(httpBasic("admin", "admin123")))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestForInvalidPayload() throws Exception {
        ProjectCreateRequest request = new ProjectCreateRequest(
            "",
            null,
            null,
            BigDecimal.ZERO,
            "",
            null
        );

        mockMvc.perform(post("/api/projects")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    private ProjectResponse buildResponse(ProjectStatus status, RiskLevel riskLevel) {
        return new ProjectResponse(
            1L,
            "Projeto A",
            LocalDate.of(2026, 3, 26),
            LocalDate.of(2026, 6, 26),
            null,
            new BigDecimal("100000.00"),
            "Descricao",
            1L,
            "Gerente",
            status,
            riskLevel
        );
    }
}

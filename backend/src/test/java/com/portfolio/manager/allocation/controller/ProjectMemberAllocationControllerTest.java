package com.portfolio.manager.allocation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.manager.allocation.dto.ProjectMemberAllocationRequest;
import com.portfolio.manager.allocation.dto.ProjectMemberAllocationResponse;
import com.portfolio.manager.allocation.service.ProjectMemberAllocationService;
import com.portfolio.manager.config.SecurityConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectMemberAllocationController.class)
@Import(SecurityConfig.class)
class ProjectMemberAllocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectMemberAllocationService allocationService;

    @Test
    void shouldAllocateMember() throws Exception {
        ProjectMemberAllocationRequest request = new ProjectMemberAllocationRequest(2L);
        when(allocationService.allocate(eq(1L), any(ProjectMemberAllocationRequest.class)))
            .thenReturn(new ProjectMemberAllocationResponse(10L, 1L, 2L, "Carlos", "funcionario"));

        mockMvc.perform(post("/api/projects/1/members")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.memberName").value("Carlos"));
    }

    @Test
    void shouldListProjectMembers() throws Exception {
        when(allocationService.findAllByProjectId(1L))
            .thenReturn(List.of(new ProjectMemberAllocationResponse(10L, 1L, 2L, "Carlos", "funcionario")));

        mockMvc.perform(get("/api/projects/1/members").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].memberId").value(2L));
    }

    @Test
    void shouldRemoveProjectMember() throws Exception {
        doNothing().when(allocationService).remove(1L, 2L);

        mockMvc.perform(delete("/api/projects/1/members/2").with(httpBasic("admin", "admin123")))
            .andExpect(status().isNoContent());
    }
}

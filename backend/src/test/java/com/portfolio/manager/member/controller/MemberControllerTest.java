package com.portfolio.manager.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.manager.config.SecurityConfig;
import com.portfolio.manager.member.dto.MemberRequest;
import com.portfolio.manager.member.dto.MemberResponse;
import com.portfolio.manager.member.service.MemberService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@Import(SecurityConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    void shouldCreateMember() throws Exception {
        MemberRequest request = new MemberRequest("Ana", "funcionario");
        when(memberService.create(any(MemberRequest.class))).thenReturn(new MemberResponse(1L, "Ana", "funcionario"));

        mockMvc.perform(post("/api/members")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "http://localhost/api/members/1"))
            .andExpect(jsonPath("$.assignment").value("funcionario"));
    }

    @Test
    void shouldListMembers() throws Exception {
        when(memberService.findAll()).thenReturn(List.of(new MemberResponse(1L, "Ana", "funcionario")));

        mockMvc.perform(get("/api/members").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Ana"));
    }

    @Test
    void shouldFindMemberById() throws Exception {
        when(memberService.findById(1L)).thenReturn(new MemberResponse(1L, "Ana", "funcionario"));

        mockMvc.perform(get("/api/members/1").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }
}

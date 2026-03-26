package com.portfolio.manager.report.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portfolio.manager.config.SecurityConfig;
import com.portfolio.manager.project.enums.ProjectStatus;
import com.portfolio.manager.report.dto.PortfolioReportResponse;
import com.portfolio.manager.report.dto.PortfolioStatusSummaryResponse;
import com.portfolio.manager.report.service.PortfolioReportService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PortfolioReportController.class)
@Import(SecurityConfig.class)
class PortfolioReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioReportService portfolioReportService;

    @Test
    void shouldReturnPortfolioSummary() throws Exception {
        when(portfolioReportService.generateReport()).thenReturn(
            new PortfolioReportResponse(
                List.of(new PortfolioStatusSummaryResponse(ProjectStatus.EM_ANALISE, 2L, new BigDecimal("1000.00"))),
                25.5,
                4L
            )
        );

        mockMvc.perform(get("/api/reports/portfolio-summary").with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalUniqueAllocatedMembers").value(4))
            .andExpect(jsonPath("$.statusSummary[0].status").value("EM_ANALISE"));
    }
}

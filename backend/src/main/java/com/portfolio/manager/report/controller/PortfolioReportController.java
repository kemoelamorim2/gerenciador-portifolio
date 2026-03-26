package com.portfolio.manager.report.controller;

import com.portfolio.manager.exception.ApiErrorResponse;
import com.portfolio.manager.report.dto.PortfolioReportResponse;
import com.portfolio.manager.report.service.PortfolioReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class PortfolioReportController {

    private final PortfolioReportService portfolioReportService;

    public PortfolioReportController(PortfolioReportService portfolioReportService) {
        this.portfolioReportService = portfolioReportService;
    }

    @GetMapping("/portfolio-summary")
    @Operation(summary = "Gera o relatorio resumido do portfolio")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatorio gerado com sucesso"),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno ao gerar relatorio",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    public ResponseEntity<PortfolioReportResponse> getPortfolioSummary() {
        return ResponseEntity.ok(portfolioReportService.generateReport());
    }
}

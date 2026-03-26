package com.portfolio.manager.report.controller;

import com.portfolio.manager.report.dto.PortfolioReportResponse;
import com.portfolio.manager.report.service.PortfolioReportService;
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
    public ResponseEntity<PortfolioReportResponse> getPortfolioSummary() {
        return ResponseEntity.ok(portfolioReportService.generateReport());
    }
}

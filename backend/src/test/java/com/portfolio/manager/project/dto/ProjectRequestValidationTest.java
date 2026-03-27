package com.portfolio.manager.project.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldValidateRequiredFieldsForProjectCreateRequest() {
        ProjectCreateRequest request = new ProjectCreateRequest(
            "",
            null,
            null,
            null,
            BigDecimal.ZERO,
            "",
            null,
            null
        );

        Set<?> violations = validator.validate(request);

        assertEquals(6, violations.size());
    }

    @Test
    void shouldAcceptValidProjectCreateRequest() {
        ProjectCreateRequest request = new ProjectCreateRequest(
            "Projeto XPTO",
            LocalDate.now(),
            LocalDate.now().plusDays(30),
            null,
            new BigDecimal("120000.00"),
            "Descricao valida",
            1L,
            null
        );

        Set<?> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRequireStatusOnProjectStatusUpdateRequest() {
        ProjectStatusUpdateRequest request = new ProjectStatusUpdateRequest(null);

        Set<?> violations = validator.validate(request);

        assertEquals(1, violations.size());
    }
}

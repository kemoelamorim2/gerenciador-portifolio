package com.portfolio.manager.member.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldValidateRequiredFields() {
        MemberRequest request = new MemberRequest("", "");

        Set<?> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }

    @Test
    void shouldAcceptValidMemberRequest() {
        MemberRequest request = new MemberRequest("Maria", "funcionario");

        Set<?> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}

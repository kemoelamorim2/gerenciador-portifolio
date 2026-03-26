package com.portfolio.manager.exception.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.portfolio.manager.exception.BusinessRuleException;
import com.portfolio.manager.exception.ExternalIntegrationException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnNotFoundForMissingResource() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/projects/1");

        var response = handler.handleResourceNotFound(
            new ResourceNotFoundException("Project not found"),
            request
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Project not found", response.getBody().message());
    }

    @Test
    void shouldReturnBadRequestForBusinessRuleViolation() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/projects");

        var response = handler.handleBusinessRule(
            new BusinessRuleException("Invalid project transition"),
            request
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid project transition", response.getBody().message());
    }

    @Test
    void shouldReturnBadGatewayForExternalIntegrationFailure() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/members/1");

        var response = handler.handleExternalIntegration(
            new ExternalIntegrationException("Member service unavailable"),
            request
        );

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("Member service unavailable", response.getBody().message());
    }

    @Test
    void shouldReturnValidationErrorsWithFieldMessages() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/projects");
        MethodParameter methodParameter = new MethodParameter(
            TestController.class.getDeclaredMethod("create", TestRequest.class),
            0
        );

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TestRequest(), "testRequest");
        bindingResult.addError(new FieldError("testRequest", "name", "must not be blank"));
        bindingResult.addError(new FieldError("testRequest", "budget", "must be greater than zero"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            methodParameter,
            bindingResult
        );

        var response = handler.handleValidation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
            "name: must not be blank; budget: must be greater than zero",
            response.getBody().message()
        );
    }

    @Test
    void shouldReturnInternalServerErrorForUnexpectedExceptions() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/projects");

        var response = handler.handleUnexpected(new IllegalStateException("boom"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected internal server error", response.getBody().message());
    }

    @RestController
    private static class TestController {
        @PostMapping
        public void create(@RequestBody TestRequest request) {
        }
    }

    private static class TestRequest {
    }
}

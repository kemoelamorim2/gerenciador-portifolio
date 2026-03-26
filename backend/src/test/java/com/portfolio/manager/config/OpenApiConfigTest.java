package com.portfolio.manager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    @Test
    void shouldCreateOpenApiDefinition() {
        OpenAPI openAPI = new OpenApiConfig().openAPI();

        assertNotNull(openAPI);
        assertEquals("Gerenciador de Portifolio API", openAPI.getInfo().getTitle());
        assertNotNull(openAPI.getComponents().getSecuritySchemes().get("basicAuth"));
    }
}

package com.portfolio.manager.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server()
            .url("http://localhost:8080")
            .description("Servidor local");

        Server prodServer = new Server()
            .url("https://api.portfolio-manager.com")
            .description("Servidor de produção");

        SecurityScheme basicAuthScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("basic")
            .description("Autenticação HTTP Basic para acesso aos endpoints protegidos");

        return new OpenAPI()
            .info(new Info()
                .title("Portfólio Manager API")
                .version("v1.0.0")
                .description(
                    "API para gestão do portfólio de projetos, incluindo projetos, membros, alocações e indicadores consolidados."
                )
                .contact(new Contact()
                    .name("Kemoel Amorim")
                    .email("contato@portfolio-manager.com")
                    .url("https://portfolio-manager.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://springdoc.org")))
            .servers(List.of(localServer, prodServer))
            .externalDocs(new ExternalDocumentation()
                .description("Guia de uso da API")
                .url("https://portfolio-manager.com/docs"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("BasicAuth", basicAuthScheme))
            .addSecurityItem(new SecurityRequirement().addList("BasicAuth"));
    }
}

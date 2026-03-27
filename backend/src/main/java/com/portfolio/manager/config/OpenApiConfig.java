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
                .description("Servidor Local");

        Server prodServer = new Server()
                .url("https://api.gerenciador.com")
                .description("Servidor Produção");

        // Define o esquema de segurança JWT
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Token");

        // Define o esquema de segurança API Key
        SecurityScheme apiKeyScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-Key")
                .description("API Key para autenticação");

        return new OpenAPI()
                .info(new Info()
                        .title("Gerenciador API")
                        .version("v1.0.0")
                        .description("API responsável por gerenciamento de pagamentos, assinaturas e multi-tenant.")
                        .contact(new Contact()
                                .name("Kemoel Amorim")
                                .email("contato@gerenciador.com")
                                .url("https://gerenciador.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(localServer, prodServer))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação Completa")
                        .url("https://docs.gerenciador.com"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("JWT", jwtScheme)
                        .addSecuritySchemes("ApiKey", apiKeyScheme))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .addSecurityItem(new SecurityRequirement().addList("ApiKey"));
    }
}

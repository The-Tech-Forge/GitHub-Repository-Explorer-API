package com.githubexplorer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI githubExplorerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GitHub Repository Explorer API")
                        .description("""
                                REST API for searching GitHub repositories,
                                storing repository information in PostgreSQL,
                                and retrieving saved repositories with filters.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")));
    }
}
package com.githubexplorer.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.githubexplorer.dto.github.GithubSearchApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GithubApiClientTest {

    private GithubApiClient githubApiClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {

        ObjectMapper objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(new JavaTimeModule());

        RestClient.Builder builder = RestClient.builder();

        builder.messageConverters(converters -> {
            converters.removeIf(
                    MappingJackson2HttpMessageConverter.class::isInstance
            );

            converters.add(
                    0,
                    new MappingJackson2HttpMessageConverter(objectMapper)
            );
        });

        mockServer = MockRestServiceServer.bindTo(builder).build();

        githubApiClient = new GithubApiClient(
                builder,
                "https://api.github.com"
        );
    }

    @Test
    void shouldSearchRepositoriesSuccessfully() {

        String responseJson = """
                {
                    "total_count": 1,
                    "incomplete_results": false,
                    "items": [
                        {
                            "id": 123,
                            "name": "spring-boot",
                            "full_name": "owner/spring-boot",
                            "description": "Spring Boot repository",
                            "owner": {
                                "login": "owner"
                            },
                            "html_url": "https://github.com/owner/spring-boot",
                            "language": "Java",
                            "stargazers_count": 100,
                            "forks_count": 20,
                            "created_at": "2024-01-01T00:00:00Z",
                            "updated_at": "2026-11-19T10:00:00Z"
                        }
                    ]
                }
                """;

        mockServer.expect(request -> {

            URI uri = request.getURI();

            assertEquals("/search/repositories", uri.getPath());

            String rawQuery = uri.getRawQuery();

            assertNotNull(rawQuery);
            assertTrue(rawQuery.contains("q=spring%20boot"));
            assertTrue(rawQuery.contains("sort=stars"));
            assertTrue(rawQuery.contains("order=desc"));

        }).andRespond(
                withSuccess(responseJson, MediaType.APPLICATION_JSON)
        );

        GithubSearchApiResponse response =
                githubApiClient.searchRepositories("spring boot", "stars");

        assertNotNull(response);
        assertEquals(1, response.getTotalCount());
        assertFalse(response.getIncompleteResults());

        assertNotNull(response.getItems());
        assertEquals(1, response.getItems().size());

        var repository = response.getItems().get(0);

        assertEquals(123L, repository.getId());
        assertEquals("spring-boot", repository.getName());
        assertEquals("owner/spring-boot", repository.getFullName());
        assertEquals("Spring Boot repository", repository.getDescription());
        assertEquals("owner", repository.getOwner().getLogin());
        assertEquals("Java", repository.getLanguage());
        assertEquals(100, repository.getStargazersCount());
        assertEquals(20, repository.getForksCount());

        mockServer.verify();
    }

    @Test
    void shouldReturnEmptyItemsWhenGithubFindsNoRepositories() {

        String responseJson = """
            {
                "total_count": 0,
                "incomplete_results": false,
                "items": []
            }
            """;

        mockServer.expect(request ->
                assertEquals(
                        "/search/repositories",
                        request.getURI().getPath()
                )
        ).andRespond(
                withSuccess(responseJson, MediaType.APPLICATION_JSON)
        );

        GithubSearchApiResponse response =
                githubApiClient.searchRepositories("non-existing-repository", "stars");

        assertNotNull(response);
        assertEquals(0, response.getTotalCount());
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());

        mockServer.verify();
    }

    @Test
    void shouldThrowExceptionWhenGithubReturnsForbidden() {

        mockServer.expect(request ->
                assertEquals(
                        "/search/repositories",
                        request.getURI().getPath()
                )
        ).andRespond(
                org.springframework.test.web.client.response.MockRestResponseCreators
                        .withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                            {
                                "message": "API rate limit exceeded"
                            }
                            """)
        );

        assertThrows(
                HttpClientErrorException.Forbidden.class,
                () -> githubApiClient.searchRepositories("spring boot", "stars")
        );

        mockServer.verify();
    }

    @Test
    void shouldThrowExceptionWhenGithubReturnsInvalidJson() {

        String invalidJson = """
            {
                "total_count": 1,
                "items": [
                    {
                        "id": 123,
                        "name":
                    }
                ]
            }
            """;

        mockServer.expect(request ->
                assertEquals(
                        "/search/repositories",
                        request.getURI().getPath()
                )
        ).andRespond(
                withSuccess(invalidJson, MediaType.APPLICATION_JSON)
        );

        assertThrows(
                RestClientException.class,
                () -> githubApiClient.searchRepositories("spring boot", "stars")
        );

        mockServer.verify();
    }
}
package com.githubexplorer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubexplorer.dto.request.GithubSearchRequest;
import com.githubexplorer.dto.response.GithubRepositoryResponse;
import com.githubexplorer.dto.response.GithubSearchResponse;
import com.githubexplorer.enums.RepositorySortType;
import com.githubexplorer.service.GithubRepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.verifyNoInteractions;
import org.springframework.web.client.ResourceAccessException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.verify;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GithubRepositoryController.class)
class GithubRepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GithubRepositoryService githubRepositoryService;

    @Test
    void shouldReturnRepositoriesWhenSearchRequestIsValid() throws Exception {

        // Arrange
        GithubRepositoryResponse repository = new GithubRepositoryResponse();
        repository.setId(123L);
        repository.setName("spring-boot");
        repository.setOwner("spring-projects");
        repository.setLanguage("Java");
        repository.setStars(500);
        repository.setForks(200);

        GithubSearchResponse serviceResponse = new GithubSearchResponse();
        serviceResponse.setMessage("Repositories fetched and saved successfully");
        serviceResponse.setRepositories(List.of(repository));

        when(githubRepositoryService.searchRepositories(any(GithubSearchRequest.class)))
                .thenReturn(serviceResponse);

        GithubSearchRequest request = new GithubSearchRequest();
        request.setQuery("spring boot");
        request.setLanguage("Java");
        request.setSort(RepositorySortType.STARS);

        // Act & Assert
        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Repositories fetched and saved successfully"))
                .andExpect(jsonPath("$.repositories[0].id").value(123))
                .andExpect(jsonPath("$.repositories[0].name").value("spring-boot"))
                .andExpect(jsonPath("$.repositories[0].owner").value("spring-projects"))
                .andExpect(jsonPath("$.repositories[0].language").value("Java"));
    }

    @Test
    void shouldReturnBadRequestWhenQueryIsEmpty() throws Exception {

        String request = """
            {
                "query": "",
                "language": "Java",
                "sort": "stars"
            }
            """;

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Search query cannot be empty"))
                .andExpect(jsonPath("$.path").value("/api/github/search"));

        verifyNoInteractions(githubRepositoryService);
    }

    @Test
    void shouldReturnBadRequestWhenSortValueIsInvalid() throws Exception {

        String request = """
            {
                "query": "spring boot",
                "language": "Java",
                "sort": "popularity"
            }
            """;

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed or invalid request body"));

        verifyNoInteractions(githubRepositoryService);
    }

    @Test
    void shouldReturnBadRequestWhenQueryIsMissing() throws Exception {

        String request = """
            {
                "language": "Java",
                "sort": "stars"
            }
            """;

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(githubRepositoryService);
    }

    @Test
    void shouldReturnBadGatewayWhenGithubApiFails() throws Exception {

        // Arrange
        when(githubRepositoryService.searchRepositories(any(GithubSearchRequest.class)))
                .thenThrow(new ResourceAccessException("Connection timed out"));

        String request = """
            {
                "query": "spring boot",
                "language": "Java",
                "sort": "stars"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"))
                .andExpect(jsonPath("$.message")
                        .value("Unable to communicate with GitHub API"))
                .andExpect(jsonPath("$.path").value("/api/github/search"));
    }

    @Test
    void shouldReturnRepositoriesWithFilters() throws Exception {

        // Arrange
        GithubRepositoryResponse repository = new GithubRepositoryResponse();
        repository.setId(123L);
        repository.setName("spring-boot");
        repository.setOwner("spring-projects");
        repository.setLanguage("Java");
        repository.setStars(500);
        repository.setForks(200);

        when(githubRepositoryService.getRepositories(
                "Java",
                100,
                RepositorySortType.FORKS
        )).thenReturn(List.of(repository));

        // Act & Assert
        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "100")
                        .param("sort", "forks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(123))
                .andExpect(jsonPath("$[0].name").value("spring-boot"))
                .andExpect(jsonPath("$[0].language").value("Java"))
                .andExpect(jsonPath("$[0].stars").value(500));

        verify(githubRepositoryService).getRepositories(
                "Java",
                100,
                RepositorySortType.FORKS
        );
    }

    @Test
    void shouldUseStarsAsDefaultSort() throws Exception {

        // Arrange
        when(githubRepositoryService.getRepositories(
                null,
                null,
                RepositorySortType.STARS
        )).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/github/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(githubRepositoryService).getRepositories(
                null,
                null,
                RepositorySortType.STARS
        );
    }

    @Test
    void shouldReturnBadRequestWhenGetSortIsInvalid() throws Exception {

        mockMvc.perform(get("/api/github/repositories")
                        .param("sort", "popularity"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request parameter"));

        verifyNoInteractions(githubRepositoryService);
    }

    @Test
    void shouldReturnBadRequestWhenMinStarsIsNegative() throws Exception {

        mockMvc.perform(get("/api/github/repositories")
                        .param("minStars", "-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Minimum stars cannot be negative"));

        verifyNoInteractions(githubRepositoryService);
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() throws Exception {

        when(githubRepositoryService.searchRepositories(any(GithubSearchRequest.class)))
                .thenThrow(new RuntimeException("Simulated unexpected error"));

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "query": "spring boot",
                                "language": "Java",
                                "sort": "stars"
                            }
                            """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred"))
                .andExpect(jsonPath("$.path").value("/api/github/search"));
    }
}
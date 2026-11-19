package com.githubexplorer.mapper;

import com.githubexplorer.dto.github.GithubApiOwnerResponse;
import com.githubexplorer.dto.github.GithubApiRepositoryResponse;
import com.githubexplorer.entity.GithubRepositoryEntity;
import com.githubexplorer.dto.response.GithubRepositoryResponse;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GithubRepositoryMapperTest {

    private final GithubRepositoryMapper mapper = new GithubRepositoryMapper();

    @Test
    void shouldMapGithubApiResponseToEntity() {
        // Arrange
        GithubApiOwnerResponse owner = new GithubApiOwnerResponse();
        owner.setLogin("spring-projects");

        OffsetDateTime createdAt =
                OffsetDateTime.parse("2024-01-01T10:00:00Z");

        OffsetDateTime updatedAt =
                OffsetDateTime.parse("2024-06-01T12:00:00Z");

        GithubApiRepositoryResponse apiResponse =
                new GithubApiRepositoryResponse();

        // Set values using setters if available.
        // Otherwise, use the getters/setters supported by your DTO.

        apiResponse.setId(123L);
        apiResponse.setName("spring-boot");
        apiResponse.setDescription("Spring Boot repository");
        apiResponse.setOwner(owner);
        apiResponse.setLanguage("Java");
        apiResponse.setStargazersCount(500);
        apiResponse.setForksCount(200);
        apiResponse.setHtmlUrl("https://github.com/spring-projects/spring-boot");
        apiResponse.setCreatedAt(createdAt);
        apiResponse.setUpdatedAt(updatedAt);

        // Act
        GithubRepositoryEntity entity = mapper.toEntity(apiResponse);

        // Assert
        assertEquals(123L, entity.getId());
        assertEquals("spring-boot", entity.getName());
        assertEquals("Spring Boot repository", entity.getDescription());
        assertEquals("spring-projects", entity.getOwnerName());
        assertEquals("Java", entity.getLanguage());
        assertEquals(500, entity.getStars());
        assertEquals(200, entity.getForks());
        assertEquals(
                "https://github.com/spring-projects/spring-boot",
                entity.getHtmlUrl()
        );
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getLastUpdated());
    }

    @Test
    void shouldMapGithubApiResponseToResponseDto() {
        // Arrange
        GithubApiOwnerResponse owner = new GithubApiOwnerResponse();
        owner.setLogin("spring-projects");

        OffsetDateTime updatedAt =
                OffsetDateTime.parse("2024-06-01T12:00:00Z");

        GithubApiRepositoryResponse apiResponse =
                new GithubApiRepositoryResponse();

        apiResponse.setId(123L);
        apiResponse.setName("spring-boot");
        apiResponse.setDescription("Spring Boot repository");
        apiResponse.setOwner(owner);
        apiResponse.setLanguage("Java");
        apiResponse.setStargazersCount(500);
        apiResponse.setForksCount(200);
        apiResponse.setUpdatedAt(updatedAt);

        // Act
        GithubRepositoryResponse response = mapper.toResponse(apiResponse);

        // Assert
        assertEquals(123L, response.getId());
        assertEquals("spring-boot", response.getName());
        assertEquals("Spring Boot repository", response.getDescription());
        assertEquals("spring-projects", response.getOwner());
        assertEquals("Java", response.getLanguage());
        assertEquals(500, response.getStars());
        assertEquals(200, response.getForks());
        assertEquals(updatedAt, response.getLastUpdated());
    }
}
package com.githubexplorer.service;

import com.githubexplorer.client.GithubApiClient;
import com.githubexplorer.dto.github.GithubApiRepositoryResponse;
import com.githubexplorer.dto.github.GithubSearchApiResponse;
import com.githubexplorer.dto.request.GithubSearchRequest;
import com.githubexplorer.dto.response.GithubRepositoryResponse;
import com.githubexplorer.dto.response.GithubSearchResponse;
import com.githubexplorer.entity.GithubRepositoryEntity;
import com.githubexplorer.enums.RepositorySortType;
import com.githubexplorer.mapper.GithubRepositoryMapper;
import com.githubexplorer.repository.GithubRepositoryBulkWriter;
import com.githubexplorer.repository.GithubRepositoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.Sort;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceTest {

    @Mock
    private GithubRepositoryRepository githubRepositoryRepository;

    @Mock
    private GithubApiClient githubApiClient;

    @Mock
    private GithubRepositoryMapper githubRepositoryMapper;

    @Mock
    private GithubRepositoryBulkWriter githubRepositoryBulkWriter;

    @InjectMocks
    private GithubRepositoryService githubRepositoryService;

    @Test
    void shouldSearchAndSaveRepositoriesSuccessfully() {

        // Arrange
        GithubSearchRequest request = new GithubSearchRequest();
        request.setQuery("spring boot");
        request.setLanguage("Java");
        request.setSort(RepositorySortType.STARS);

        GithubApiRepositoryResponse apiRepository =
                mock(GithubApiRepositoryResponse.class);

        GithubSearchApiResponse apiResponse =
                new GithubSearchApiResponse();

        apiResponse.setItems(List.of(apiRepository));

        GithubRepositoryEntity entity = new GithubRepositoryEntity();
        entity.setId(123L);

        GithubRepositoryResponse repositoryResponse =
                new GithubRepositoryResponse();

        repositoryResponse.setId(123L);
        repositoryResponse.setName("spring-boot-example");

        when(githubApiClient.searchRepositories(
                "spring boot in:name language:Java",
                "stars"
        )).thenReturn(apiResponse);

        when(githubRepositoryMapper.toEntity(apiRepository))
                .thenReturn(entity);

        when(githubRepositoryMapper.toResponse(apiRepository))
                .thenReturn(repositoryResponse);

        // Act
        GithubSearchResponse response =
                githubRepositoryService.searchRepositories(request);

        // Assert
        assertNotNull(response);

        assertEquals(
                "Repositories fetched and saved successfully",
                response.getMessage()
        );

        assertEquals(1, response.getRepositories().size());

        assertEquals(
                "spring-boot-example",
                response.getRepositories().get(0).getName()
        );

        verify(githubApiClient).searchRepositories(
                "spring boot in:name language:Java",
                "stars"
        );

        verify(githubRepositoryMapper).toEntity(apiRepository);

        verify(githubRepositoryBulkWriter).upsertAll(List.of(entity));
    }

    @Test
    void shouldReturnEmptyRepositoriesWhenGithubReturnsNoResults() {

        // Arrange
        GithubSearchRequest request = new GithubSearchRequest();
        request.setQuery("nonexistent-repository");
        request.setSort(RepositorySortType.STARS);

        GithubSearchApiResponse apiResponse =
                new GithubSearchApiResponse();

        apiResponse.setItems(List.of());

        when(githubApiClient.searchRepositories(
                "nonexistent-repository in:name",
                "stars"
        )).thenReturn(apiResponse);

        // Act
        GithubSearchResponse response =
                githubRepositoryService.searchRepositories(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getRepositories());
        assertTrue(response.getRepositories().isEmpty());

        verify(githubRepositoryBulkWriter, never()).upsertAll(anyList());
        verifyNoInteractions(githubRepositoryMapper);
    }

    @Test
    void shouldPropagateExceptionWhenGithubApiFails() {
        // Arrange
        GithubSearchRequest request = new GithubSearchRequest();
        request.setQuery("spring boot");
        request.setLanguage("Java");
        request.setSort(RepositorySortType.STARS);

        when(githubApiClient.searchRepositories(
                "spring boot in:name language:Java",
                "stars"
        )).thenThrow(new RuntimeException("GitHub API is unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> githubRepositoryService.searchRepositories(request)
        );

        assertEquals("GitHub API is unavailable", exception.getMessage());

        verifyNoInteractions(githubRepositoryBulkWriter);
        verifyNoInteractions(githubRepositoryMapper);
    }

    @Test
    void shouldSearchWithoutLanguageFilter() {
        // Arrange
        GithubSearchRequest request = new GithubSearchRequest();
        request.setQuery("spring boot");
        request.setSort(RepositorySortType.STARS);

        GithubSearchApiResponse apiResponse = new GithubSearchApiResponse();
        apiResponse.setItems(List.of());

        when(githubApiClient.searchRepositories(
                "spring boot in:name",
                "stars"
        )).thenReturn(apiResponse);

        // Act
        GithubSearchResponse response =
                githubRepositoryService.searchRepositories(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getRepositories().isEmpty());

        verify(githubApiClient).searchRepositories(
                "spring boot in:name",
                "stars"
        );
    }

    @ParameterizedTest
    @EnumSource(RepositorySortType.class)
    void shouldPassCorrectSortParameterToGithubApi(RepositorySortType sortType) {
        // Arrange
        GithubSearchRequest request = new GithubSearchRequest();
        request.setQuery("spring boot");
        request.setSort(sortType);

        GithubSearchApiResponse apiResponse = new GithubSearchApiResponse();
        apiResponse.setItems(List.of());

        when(githubApiClient.searchRepositories(
                "spring boot in:name",
                sortType.name().toLowerCase()
        )).thenReturn(apiResponse);

        // Act
        githubRepositoryService.searchRepositories(request);

        // Assert
        verify(githubApiClient).searchRepositories(
                "spring boot in:name",
                sortType.name().toLowerCase()
        );
    }

    @Test
    void shouldFetchRepositoriesWithFiltersAndSort() {
        GithubRepositoryEntity entity = new GithubRepositoryEntity();
        entity.setId(1L);
        entity.setName("spring-boot");
        entity.setOwnerName("spring-projects");
        entity.setLanguage("Java");
        entity.setStars(500);
        entity.setForks(200);

        GithubRepositoryResponse response = new GithubRepositoryResponse();
        response.setId(1L);
        response.setName("spring-boot");
        response.setOwner("spring-projects");
        response.setLanguage("Java");
        response.setStars(500);
        response.setForks(200);

        when(githubRepositoryRepository.findByFilters(
                eq("Java"),
                eq(100),
                any(Sort.class)
        )).thenReturn(List.of(entity));

        when(githubRepositoryMapper.toResponse(entity)).thenReturn(response);

        List<GithubRepositoryResponse> result =
                githubRepositoryService.getRepositories(
                        "Java",
                        100,
                        RepositorySortType.FORKS
                );

        assertEquals(1, result.size());
        assertEquals("spring-boot", result.get(0).getName());
        assertEquals(500, result.get(0).getStars());

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

        verify(githubRepositoryRepository).findByFilters(
                eq("Java"),
                eq(100),
                sortCaptor.capture()
        );

        assertEquals(
                "forks",
                sortCaptor.getValue().getOrderFor("forks").getProperty()
        );

        verify(githubRepositoryMapper).toResponse(entity);
    }

    @Test
    void shouldReturnEmptyListWhenNoRepositoriesMatchFilters() {
        when(githubRepositoryRepository.findByFilters(
                eq("Python"),
                eq(1000),
                any(Sort.class)
        )).thenReturn(List.of());

        List<GithubRepositoryResponse> result =
                githubRepositoryService.getRepositories(
                        "Python",
                        1000,
                        RepositorySortType.STARS
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(githubRepositoryRepository).findByFilters(
                eq("Python"),
                eq(1000),
                any(Sort.class)
        );

        verifyNoInteractions(githubRepositoryMapper);
    }

    @ParameterizedTest
    @EnumSource(RepositorySortType.class)
    void shouldApplyCorrectSortingForEachSortType(RepositorySortType sortType) {

        when(githubRepositoryRepository.findByFilters(
                isNull(),
                isNull(),
                any(Sort.class)
        )).thenReturn(List.of());

        githubRepositoryService.getRepositories(null, null, sortType);

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

        verify(githubRepositoryRepository).findByFilters(
                isNull(),
                isNull(),
                sortCaptor.capture()
        );

        String expectedField = switch (sortType) {
            case STARS -> "stars";
            case FORKS -> "forks";
            case UPDATED -> "lastUpdated";
        };

        Sort.Order order = sortCaptor.getValue().getOrderFor(expectedField);

        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }
}
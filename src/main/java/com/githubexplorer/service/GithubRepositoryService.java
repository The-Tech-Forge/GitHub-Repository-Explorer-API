package com.githubexplorer.service;

import com.githubexplorer.client.GithubApiClient;
import com.githubexplorer.dto.github.GithubSearchApiResponse;
import com.githubexplorer.dto.request.GithubSearchRequest;
import com.githubexplorer.dto.response.GithubRepositoryResponse;
import com.githubexplorer.dto.response.GithubSearchResponse;
import com.githubexplorer.entity.GithubRepositoryEntity;
import com.githubexplorer.mapper.GithubRepositoryMapper;
import com.githubexplorer.repository.GithubRepositoryBulkWriter;
import com.githubexplorer.repository.GithubRepositoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubRepositoryService {
    private final GithubRepositoryRepository githubRepositoryRepository;
    private final GithubApiClient githubApiClient;
    private final GithubRepositoryMapper githubRepositoryMapper;
    private final GithubRepositoryBulkWriter githubRepositoryBulkWriter;

    public GithubRepositoryService(
            GithubRepositoryRepository githubRepositoryRepository,
            GithubApiClient githubApiClient,
            GithubRepositoryMapper githubRepositoryMapper,
            GithubRepositoryBulkWriter githubRepositoryBulkWriter
            ){
        this.githubRepositoryRepository = githubRepositoryRepository;
        this.githubApiClient = githubApiClient;
        this.githubRepositoryMapper = githubRepositoryMapper;
        this.githubRepositoryBulkWriter = githubRepositoryBulkWriter;
    }

    private String buildSearchQuery(String query, String language) {

        StringBuilder searchQuery = new StringBuilder(query);

        searchQuery.append(" in:name");

        if (language != null && !language.isBlank()) {
            searchQuery.append(" language:").append(language);
        }

        return searchQuery.toString();
    }

    public GithubSearchResponse searchRepositories(GithubSearchRequest request) {

        String searchQuery = buildSearchQuery(
                request.getQuery(),
                request.getLanguage()
        );

        String sort = request.getSort().name().toLowerCase();
        GithubSearchApiResponse githubSearchApiResponse = githubApiClient.searchRepositories(searchQuery, sort);

//        // Handle empty results
//        if (githubSearchApiResponse == null || githubSearchApiResponse.getItems() == null
//                || githubSearchApiResponse.getItems().isEmpty()) {
//            return githubSearchApiResponse;
//        }

        List<GithubRepositoryResponse> repositories = githubSearchApiResponse.getItems()
                .stream()
                .map(githubRepositoryMapper::toResponse)
                .toList();

        GithubSearchResponse searchResponse = new GithubSearchResponse();

        searchResponse.setMessage(
                "Repositories fetched and saved successfully"
        );
        searchResponse.setRepositories(repositories);

        return searchResponse;
    }
}

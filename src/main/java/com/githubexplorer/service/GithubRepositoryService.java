package com.githubexplorer.service;

import com.githubexplorer.client.GithubApiClient;
import com.githubexplorer.dto.github.GithubSearchApiResponse;
import com.githubexplorer.dto.request.GithubSearchRequest;
import com.githubexplorer.repository.GithubRepositoryRepository;
import org.springframework.stereotype.Service;

@Service
public class GithubRepositoryService {
    private final GithubRepositoryRepository githubRepositoryRepository;
    private final GithubApiClient githubApiClient;

    public GithubRepositoryService(
            GithubRepositoryRepository githubRepositoryRepository,
            GithubApiClient githubApiClient){
        this.githubRepositoryRepository = githubRepositoryRepository;
        this.githubApiClient = githubApiClient;
    }

    private String buildSearchQuery(String query, String language) {

        StringBuilder searchQuery = new StringBuilder(query);

        searchQuery.append(" in:name");

        if (language != null && !language.isBlank()) {
            searchQuery.append(" language:").append(language);
        }

        return searchQuery.toString();
    }

    public GithubSearchApiResponse searchRepositories(GithubSearchRequest request) {

        String searchQuery = buildSearchQuery(
                request.getQuery(),
                request.getLanguage()
        );

        String sort = request.getSort().name().toLowerCase();

        return githubApiClient.searchRepositories(searchQuery, sort);
    }
}

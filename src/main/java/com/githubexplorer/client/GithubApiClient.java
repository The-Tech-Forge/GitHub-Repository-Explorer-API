package com.githubexplorer.client;

import com.githubexplorer.dto.github.GithubSearchApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GithubApiClient {
    private final RestClient restClient;

    public GithubApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${github.api.base-url}") String baseUrl
            ){
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public GithubSearchApiResponse searchRepositories(String query) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", query)
                        .queryParam("sort", "stars")
                        .queryParam("order", "desc")
                        .build())
                .retrieve()
                .body(GithubSearchApiResponse.class);
    }
}

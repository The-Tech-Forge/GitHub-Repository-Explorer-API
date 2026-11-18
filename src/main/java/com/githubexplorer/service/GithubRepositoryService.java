package com.githubexplorer.service;

import com.githubexplorer.client.GithubApiClient;
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
}

package com.githubexplorer.dto.github;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class GithubApiRepositoryResponse {
    private Long id;
    private String name;
    private String fullName;
    private String description;
    private GithubApiOwnerResponse owner;
    private String htmlUrl;
    private String language;
    private Integer stargazersCount;
    private Integer forksCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

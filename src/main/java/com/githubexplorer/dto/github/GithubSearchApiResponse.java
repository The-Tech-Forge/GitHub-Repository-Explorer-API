package com.githubexplorer.dto.github;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GithubSearchApiResponse {
    private Integer totalCount;
    private Boolean incompleteResults;
    private List<GithubApiRepositoryResponse> items;
}

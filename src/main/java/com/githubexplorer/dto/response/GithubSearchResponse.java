package com.githubexplorer.dto.response;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class GithubSearchResponse {

    private String message;
    private List<GithubRepositoryResponse> repositories;
}
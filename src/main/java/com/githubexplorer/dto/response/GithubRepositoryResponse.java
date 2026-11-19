package com.githubexplorer.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class GithubRepositoryResponse {

    private Long id;
    private String name;
    private String description;
    private String owner;
    private String language;
    private Integer stars;
    private Integer forks;
    private OffsetDateTime lastUpdated;
}
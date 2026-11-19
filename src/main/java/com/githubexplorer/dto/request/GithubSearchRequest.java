package com.githubexplorer.dto.request;

import com.githubexplorer.enums.RepositorySortType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubSearchRequest {

    @NotBlank(message = "Search query cannot be empty")
    private String query;
    
    private String language;

    @NotNull(message = "Sort cannot be null")
    private RepositorySortType sort = RepositorySortType.STARS;
}

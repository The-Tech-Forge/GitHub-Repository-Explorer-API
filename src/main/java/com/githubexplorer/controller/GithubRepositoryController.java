package com.githubexplorer.controller;

import com.githubexplorer.dto.github.GithubApiRepositoryResponse;
import com.githubexplorer.dto.request.GithubSearchRequest;
import com.githubexplorer.dto.response.GithubRepositoryResponse;
import com.githubexplorer.dto.response.GithubSearchResponse;
import com.githubexplorer.enums.RepositorySortType;
import com.githubexplorer.service.GithubRepositoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Validated
@RestController
@RequestMapping("/api/github")
public class GithubRepositoryController {
    public final GithubRepositoryService githubRepositoryService;

    public GithubRepositoryController(GithubRepositoryService githubRepositoryService){
        this.githubRepositoryService = githubRepositoryService;
    }

    @PostMapping("search")
    public ResponseEntity<GithubSearchResponse> searchRepositories(
            @Valid @RequestBody GithubSearchRequest request
    ) {
        GithubSearchResponse response =
                githubRepositoryService.searchRepositories(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/repositories")
    public ResponseEntity<List<GithubRepositoryResponse>> getRepositories(
            @RequestParam(required = false) String language,
            @Min(value = 0, message = "Minimum stars cannot be negative")
            @RequestParam(required = false) Integer minStars,
            @RequestParam(defaultValue = "stars") String sort
    ) {
        RepositorySortType sortType = RepositorySortType.valueOf(
                sort.toUpperCase(Locale.ROOT)
        );

        List<GithubRepositoryResponse> repositories =
                githubRepositoryService.getRepositories(
                        language,
                        minStars,
                        sortType
                );

        return ResponseEntity.ok(repositories);
    }
}

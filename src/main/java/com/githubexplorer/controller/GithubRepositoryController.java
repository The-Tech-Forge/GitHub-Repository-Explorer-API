package com.githubexplorer.controller;

import com.githubexplorer.dto.github.GithubApiRepositoryResponse;
import com.githubexplorer.dto.request.GithubSearchRequest;
import com.githubexplorer.dto.response.GithubSearchResponse;
import com.githubexplorer.service.GithubRepositoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

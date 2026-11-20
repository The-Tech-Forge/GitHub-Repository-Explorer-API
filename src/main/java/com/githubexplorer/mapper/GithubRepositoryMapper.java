package com.githubexplorer.mapper;

import com.githubexplorer.dto.github.GithubApiRepositoryResponse;
import com.githubexplorer.dto.response.GithubRepositoryResponse;
import com.githubexplorer.entity.GithubRepositoryEntity;
import org.springframework.stereotype.Component;

@Component
public class GithubRepositoryMapper {

    public GithubRepositoryEntity toEntity(GithubApiRepositoryResponse response){
        GithubRepositoryEntity githubRepositoryEntity = new GithubRepositoryEntity();
        githubRepositoryEntity.setId(response.getId());
        githubRepositoryEntity.setName(response.getName());
        githubRepositoryEntity.setDescription(response.getDescription());
        githubRepositoryEntity.setOwnerName(response.getOwner().getLogin());
        githubRepositoryEntity.setLanguage(response.getLanguage());
        githubRepositoryEntity.setStars(response.getStargazersCount());
        githubRepositoryEntity.setForks(response.getForksCount());
        githubRepositoryEntity.setHtmlUrl(response.getHtmlUrl());
        githubRepositoryEntity.setCreatedAt(response.getCreatedAt());
        githubRepositoryEntity.setLastUpdated(response.getUpdatedAt());
        return  githubRepositoryEntity;
    }

    public GithubRepositoryResponse toResponse(
            GithubApiRepositoryResponse response
    ) {
        GithubRepositoryResponse repositoryResponse =
                new GithubRepositoryResponse();

        repositoryResponse.setId(response.getId());
        repositoryResponse.setName(response.getName());
        repositoryResponse.setDescription(response.getDescription());
        repositoryResponse.setOwner(response.getOwner().getLogin());
        repositoryResponse.setLanguage(response.getLanguage());
        repositoryResponse.setStars(response.getStargazersCount());
        repositoryResponse.setForks(response.getForksCount());
        repositoryResponse.setLastUpdated(response.getUpdatedAt());

        return repositoryResponse;
    }

    public GithubRepositoryResponse toResponse(GithubRepositoryEntity entity) {
        GithubRepositoryResponse response = new GithubRepositoryResponse();

        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setOwner(entity.getOwnerName());
        response.setLanguage(entity.getLanguage());
        response.setStars(entity.getStars());
        response.setForks(entity.getForks());
        response.setLastUpdated(entity.getLastUpdated());

        return response;
    }
}

package com.githubexplorer.repository;

import com.githubexplorer.entity.GithubRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubRepositoryRepository
        extends JpaRepository<GithubRepositoryEntity,Long>{

}

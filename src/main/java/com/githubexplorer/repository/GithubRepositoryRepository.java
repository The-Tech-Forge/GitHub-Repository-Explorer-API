package com.githubexplorer.repository;

import com.githubexplorer.entity.GithubRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface GithubRepositoryRepository
        extends JpaRepository<GithubRepositoryEntity,Long>{

    @Query("""
        SELECT repository
        FROM GithubRepositoryEntity repository
        WHERE (CAST(:language AS string) IS NULL
               OR LOWER(repository.language) = LOWER(CAST(:language AS string)))
          AND (:minStars IS NULL
               OR repository.stars >= :minStars)
        """)
    List<GithubRepositoryEntity> findByFilters(
            @Param("language") String language,
            @Param("minStars") Integer minStars,
            Sort sort
    );
}

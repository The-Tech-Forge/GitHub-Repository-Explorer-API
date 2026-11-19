package com.githubexplorer.repository;

import com.githubexplorer.entity.GithubRepositoryEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GithubRepositoryBulkWriter {
    private final JdbcTemplate jdbcTemplate;

    public GithubRepositoryBulkWriter(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertAll(List<GithubRepositoryEntity> entities){
        if (entities == null || entities.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("""
            INSERT INTO github_repositories (
                id, name, description, owner_name, language,
                stars, forks, html_url, created_at, last_updated,
                modified_at
            )
            VALUES
            """);

        String rowPlaceholder = "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        for (int i = 0; i < entities.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(rowPlaceholder);
        }

        sql.append("""
            ON CONFLICT (id)
            DO UPDATE SET
                name = EXCLUDED.name,
                description = EXCLUDED.description,
                owner_name = EXCLUDED.owner_name,
                language = EXCLUDED.language,
                stars = EXCLUDED.stars,
                forks = EXCLUDED.forks,
                html_url = EXCLUDED.html_url,
                last_updated = EXCLUDED.last_updated,
                modified_at = CURRENT_TIMESTAMP
            """);

        jdbcTemplate.update(sql.toString(), ps -> {
            int index = 1;

            for (GithubRepositoryEntity entity : entities) {
                ps.setLong(index++, entity.getId());
                ps.setString(index++, entity.getName());
                ps.setString(index++, entity.getDescription());
                ps.setString(index++, entity.getOwnerName());
                ps.setString(index++, entity.getLanguage());
                ps.setInt(index++, entity.getStars() != null ? entity.getStars() : 0);
                ps.setInt(index++, entity.getForks() != null ? entity.getForks() : 0);
                ps.setString(index++, entity.getHtmlUrl());
                ps.setObject(index++, entity.getCreatedAt());
                ps.setObject(index++, entity.getLastUpdated());
            }
        });

    }
}

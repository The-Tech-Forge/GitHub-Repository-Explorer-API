CREATE TABLE github_repositories (
     id BIGINT PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     description TEXT,
     owner_name VARCHAR(255) NOT NULL,
     language VARCHAR(100),
     stars INTEGER NOT NULL DEFAULT 0,
     forks INTEGER NOT NULL DEFAULT 0,
     html_url TEXT NOT NULL,
     created_at TIMESTAMPTZ NOT NULL,
     last_updated TIMESTAMPTZ NOT NULL,
     modified_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_github_repositories_language
    ON github_repositories(language);

CREATE INDEX idx_github_repositories_stars
    ON github_repositories(stars);
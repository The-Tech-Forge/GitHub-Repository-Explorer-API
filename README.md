# GitHub Repository Explorer API

A Spring Boot REST API that allows users to search GitHub repositories, store repository information in PostgreSQL, and retrieve previously saved repositories with filtering and sorting capabilities.

The application integrates with the GitHub REST API to fetch repository information and uses PostgreSQL for persistent storage.

## Live Application

- **Base URL:** https://github-repository-explorer-api.onrender.com
- **Swagger UI:** https://github-repository-explorer-api.onrender.com/swagger-ui/index.html
- **OpenAPI Documentation:** https://github-repository-explorer-api.onrender.com/v3/api-docs

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 3.5 | Backend framework |
| Spring Web | REST API development |
| Spring Data JPA | Database operations |
| PostgreSQL | Relational database |
| Hibernate | ORM |
| Flyway | Database schema migrations |
| Maven | Dependency management and build |
| Docker & Docker Compose | Containerization |
| Swagger / OpenAPI | API documentation |
| GitHub REST API | Repository search integration |

## Features

- Search GitHub repositories using search criteria.
- Fetch repository information directly from GitHub.
- Persist repository information in PostgreSQL.
- Retrieve previously saved repositories without repeatedly calling GitHub.
- Filter repositories by programming language and minimum stars.
- Sort repositories by stars, forks, or last updated date.
- Automatically manage database schema migrations using Flyway.
- Interactive API documentation using Swagger UI.

## Prerequisites

To run the project locally, you need:

- Java 21 or later.
- Maven 3.9 or later (not required when using Docker Compose).
- Docker and Docker Compose.
- A GitHub Personal Access Token (optional, but useful for higher GitHub API rate limits).

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/The-Tech-Forge/GitHub-Repository-Explorer-API.git

cd GitHub-Repository-Explorer-API
```

### 2. Configure Environment Variables

Create a `.env.properties` file in the project root:

```env
POSTGRES_DB=github_explorer
POSTGRES_USER=github_explorer
POSTGRES_PASSWORD=your_secure_password
GITHUB_API_BASE_URL=https://api.github.com
```

Do not commit this file or expose your database credentials.

### 3. Run Using Docker Compose

Build and start the application and PostgreSQL database:

```bash
docker compose up -d --build
```

Docker Compose starts:
- Spring Boot application.
- PostgreSQL database.

The application will be accessible at:

```text
http://localhost:8081
```

### 4. Access Swagger UI

Open:

```text
http://localhost:8081/swagger-ui/index.html
```

You can explore and execute the available API endpoints directly from Swagger UI.

### 5. Stop the Application

```bash
docker compose down
```

To remove the PostgreSQL volume and its stored data as well:

```bash
docker compose down -v
```

**Warning:** The second command permanently deletes the database data stored in the Compose volume.

## Running Without Docker

If you prefer running Spring Boot directly:

### 1. Start PostgreSQL

Create a PostgreSQL database with the following details:

```text
Database: github_explorer
Username: github_explorer
Password: your_secure_password
```

### 2. Configure Application Environment

Create a `.env.properties` file in the project root:

```properties
DB_URL=jdbc:postgresql://localhost:5432/github_explorer
DB_USERNAME=github_explorer
DB_PASSWORD=your_secure_password
GITHUB_API_BASE_URL=https://api.github.com
GITHUB_API_TOKEN=
```

The GitHub API token is optional.

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

Or, if Maven is installed:

```bash
mvn spring-boot:run
```

The application will start on port `8080` by default.

Flyway automatically applies pending database migrations during application startup.

## API Documentation

Base URL:

```text
https://github-repository-explorer-api.onrender.com
```

### 1. Search GitHub Repositories

**Endpoint:**

```http
POST /api/github/search
```

Searches GitHub repositories based on the supplied criteria and saves the retrieved repository information in PostgreSQL.

**Request Body:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| query | String | Yes | Search keywords for repositories |
| language | String | No | Filter repositories by programming language |
| sort | String | No | Sorting criteria for GitHub search results |

**Example Request:**

```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

**Example Response:**

```json
{
  "message": "Repositories fetched and saved successfully",
  "repositories": [
    {
      "id": 6296790,
      "name": "spring-boot",
      "description": "Spring Boot helps you to create Spring-powered, production-grade applications and services.",
      "owner": "spring-projects",
      "language": "Java",
      "stars": 81913,
      "forks": 42714,
      "lastUpdated": "2026-11-20T07:20:51Z"
    }
  ]
}
```

The response contains the retrieved repository information. The same repository data is persisted in PostgreSQL.

### 2. Retrieve Stored Repositories

**Endpoint:**

```http
GET /api/github/repositories
```

Retrieves repositories previously stored in the database.

This endpoint supports filtering and sorting without making another GitHub API search request.

**Query Parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| language | String | No | Filter repositories by programming language |
| minStars | Integer | No | Retrieve repositories having at least the specified number of stars |
| sort | String | No | Sort results by `stars`, `forks`, or `updated` (default: `stars`) |

**Example Request:**

```http
GET /api/github/repositories?language=Java&minStars=1000&sort=stars
```

**Example Response:**

```json
{
  "repositories": [
    {
      "id": 6296790,
      "name": "spring-boot",
      "description": "Spring Boot helps you to create Spring-powered applications.",
      "owner": "spring-projects",
      "language": "Java",
      "stars": 81913,
      "forks": 42714,
      "lastUpdated": "2026-11-20T07:20:51Z"
    }
  ]
}
```

The endpoint
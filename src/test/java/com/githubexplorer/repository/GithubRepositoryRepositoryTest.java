package com.githubexplorer.repository;

import com.githubexplorer.entity.GithubRepositoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class GithubRepositoryRepositoryTest {

    @Autowired
    private GithubRepositoryRepository githubRepositoryRepository;

    @Test
    void shouldFindRepositoriesByLanguageAndMinimumStars() {

        // Arrange: Create test repository
        GithubRepositoryEntity entity = new GithubRepositoryEntity();

        entity.setId(999999L);
        entity.setName("spring-test");
        entity.setDescription("Test repository");
        entity.setOwnerName("test-owner");
        entity.setLanguage("RepositoryTestLanguage");
        entity.setStars(500);
        entity.setForks(100);
        entity.setHtmlUrl("https://github.com/test/spring-test");

        OffsetDateTime now = OffsetDateTime.now();

        entity.setCreatedAt(now);
        entity.setLastUpdated(now);
        entity.setModifiedAt(now);

        githubRepositoryRepository.saveAndFlush(entity);

        // Act: Execute repository query
        List<GithubRepositoryEntity> result =
                githubRepositoryRepository.findByFilters(
                        "RepositoryTestLanguage",
                        100,
                        Sort.by(Sort.Direction.DESC, "stars")
                );

        // Assert: Verify query results
        assertEquals(1, result.size());

        GithubRepositoryEntity foundRepository = result.get(0);

        assertEquals(999999L, foundRepository.getId());
        assertEquals("spring-test", foundRepository.getName());
        assertEquals("RepositoryTestLanguage", foundRepository.getLanguage());
        assertEquals(500, foundRepository.getStars());
        assertEquals(100, foundRepository.getForks());
    }

    private GithubRepositoryEntity createRepository(Long id, String language) {
        GithubRepositoryEntity entity = new GithubRepositoryEntity();

        OffsetDateTime now = OffsetDateTime.now();

        entity.setId(id);
        entity.setName("test-repository-" + id);
        entity.setDescription("Repository integration test");
        entity.setOwnerName("test-owner");
        entity.setLanguage(language);
        entity.setStars(500);
        entity.setForks(100);
        entity.setHtmlUrl("https://github.com/test/repository-" + id);
        entity.setCreatedAt(now);
        entity.setLastUpdated(now);
        entity.setModifiedAt(now);

        return entity;
    }

    @Test
    void shouldFilterRepositoriesByLanguageCaseInsensitively() {

        GithubRepositoryEntity javaRepository = createRepository(
                999998L, "JavaCaseTest"
        );

        GithubRepositoryEntity pythonRepository = createRepository(
                999997L, "Python"
        );

        githubRepositoryRepository.saveAllAndFlush(
                List.of(javaRepository, pythonRepository)
        );

        List<GithubRepositoryEntity> result =
                githubRepositoryRepository.findByFilters(
                        "javacasetest",
                        null,
                        Sort.by(Sort.Direction.DESC, "stars")
                );

        assertEquals(1, result.size());
        assertEquals(999998L, result.get(0).getId());
        assertEquals("JavaCaseTest", result.get(0).getLanguage());
    }

    @Test
    void shouldFilterRepositoriesByMinimumStars() {

        GithubRepositoryEntity popularRepository =
                createRepository(999996L, "StarsTestLanguage");
        popularRepository.setStars(1000);

        GithubRepositoryEntity lessPopularRepository =
                createRepository(999995L, "StarsTestLanguage");
        lessPopularRepository.setStars(50);

        githubRepositoryRepository.saveAllAndFlush(
                List.of(popularRepository, lessPopularRepository)
        );

        List<GithubRepositoryEntity> result =
                githubRepositoryRepository.findByFilters(
                        "StarsTestLanguage",
                        100,
                        Sort.by(Sort.Direction.DESC, "stars")
                );

        assertEquals(1, result.size());
        assertEquals(999996L, result.get(0).getId());
        assertEquals(1000, result.get(0).getStars());
    }

    @Test
    void shouldSortRepositoriesByForksInDescendingOrder() {

        GithubRepositoryEntity popularRepository =
                createRepository(999994L, "SortingTestLanguage");
        popularRepository.setForks(500);

        GithubRepositoryEntity lessPopularRepository =
                createRepository(999993L, "SortingTestLanguage");
        lessPopularRepository.setForks(100);

        githubRepositoryRepository.saveAllAndFlush(
                List.of(lessPopularRepository, popularRepository)
        );

        List<GithubRepositoryEntity> result =
                githubRepositoryRepository.findByFilters(
                        "SortingTestLanguage",
                        null,
                        Sort.by(Sort.Direction.DESC, "forks")
                );

        assertEquals(2, result.size());
        assertEquals(999994L, result.get(0).getId());
        assertEquals(999993L, result.get(1).getId());
    }

    @Test
    void shouldSortRepositoriesByLastUpdatedInDescendingOrder() {
        // Arrange
        GithubRepositoryEntity olderRepository =
                createRepository(999994L, "OlderRepository");

        olderRepository.setLanguage("LastUpdatedTestLanguage");
        olderRepository.setLastUpdated(
                OffsetDateTime.now().minusDays(5)
        );

        GithubRepositoryEntity newerRepository =
                createRepository(999993L, "NewerRepository");

        newerRepository.setLanguage("LastUpdatedTestLanguage");
        newerRepository.setLastUpdated(
                OffsetDateTime.now()
        );

        githubRepositoryRepository.saveAllAndFlush(
                List.of(olderRepository, newerRepository)
        );

        // Act
        List<GithubRepositoryEntity> result =
                githubRepositoryRepository.findByFilters(
                        "LastUpdatedTestLanguage",
                        null,
                        Sort.by(Sort.Direction.DESC, "lastUpdated")
                );

        // Assert
        assertEquals(2, result.size());
        assertEquals(999993L, result.get(0).getId());
        assertEquals(999994L, result.get(1).getId());
    }

    @Test
    void shouldSortRepositoriesByStarsInDescendingOrder() {
        // Arrange
        GithubRepositoryEntity lowStarsRepository =
                createRepository(999996L, "LowStarsRepository");

        lowStarsRepository.setLanguage("StarsSortingTestLanguage");
        lowStarsRepository.setStars(100);

        GithubRepositoryEntity highStarsRepository =
                createRepository(999995L, "HighStarsRepository");

        highStarsRepository.setLanguage("StarsSortingTestLanguage");
        highStarsRepository.setStars(1000);

        GithubRepositoryEntity mediumStarsRepository =
                createRepository(999997L, "MediumStarsRepository");

        mediumStarsRepository.setLanguage("StarsSortingTestLanguage");
        mediumStarsRepository.setStars(500);

        githubRepositoryRepository.saveAllAndFlush(
                List.of(
                        lowStarsRepository,
                        highStarsRepository,
                        mediumStarsRepository
                )
        );

        // Act
        List<GithubRepositoryEntity> result =
                githubRepositoryRepository.findByFilters(
                        "StarsSortingTestLanguage",
                        null,
                        Sort.by(Sort.Direction.DESC, "stars")
                );

        // Assert
        assertEquals(3, result.size());
        assertEquals(999995L, result.get(0).getId());
        assertEquals(999997L, result.get(1).getId());
        assertEquals(999996L, result.get(2).getId());
    }

    @Test
    void shouldFilterByMinimumStarsWhenLanguageIsNull() {
        // Arrange
        GithubRepositoryEntity highStarsRepository =
                createRepository(777001L, "HighStarsRepository");

        highStarsRepository.setStars(500);

        GithubRepositoryEntity lowStarsRepository =
                createRepository(777002L, "LowStarsRepository");

        lowStarsRepository.setStars(50);

        githubRepositoryRepository.saveAllAndFlush(
                List.of(highStarsRepository, lowStarsRepository)
        );

        // Act
        List<GithubRepositoryEntity> result =
                githubRepositoryRepository.findByFilters(
                        null,
                        100,
                        Sort.by(Sort.Direction.DESC, "stars")
                );

        // Assert
        List<Long> resultIds = result.stream()
                .map(GithubRepositoryEntity::getId)
                .toList();

        assertTrue(resultIds.contains(777001L));
        assertFalse(resultIds.contains(777002L));
    }

    @Test
    void shouldReturnEmptyListWhenNoRepositoriesMatchFilters() {
        // Arrange
        GithubRepositoryEntity repository =
                createRepository(777003L, "TestRepository");

        repository.setLanguage("EmptyResultTestLanguage");
        repository.setStars(50);

        githubRepositoryRepository.saveAndFlush(repository);

        // Act
        List<GithubRepositoryEntity> result =
                githubRepositoryRepository.findByFilters(
                        "NonExistingLanguage",
                        100,
                        Sort.by(Sort.Direction.DESC, "stars")
                );

        // Assert
        assertTrue(result.isEmpty());
    }
}
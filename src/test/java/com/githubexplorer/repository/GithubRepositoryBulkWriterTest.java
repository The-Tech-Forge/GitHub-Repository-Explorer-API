package com.githubexplorer.repository;

import com.githubexplorer.entity.GithubRepositoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(GithubRepositoryBulkWriter.class)
class GithubRepositoryBulkWriterTest {

    @Autowired
    private GithubRepositoryBulkWriter bulkWriter;

    @Autowired
    private GithubRepositoryRepository repository;

    @Test
    void shouldUpdateExistingRepositoryInsteadOfCreatingDuplicate() {

        GithubRepositoryEntity entity = new GithubRepositoryEntity();

        OffsetDateTime now = OffsetDateTime.now();

        entity.setId(888888L);
        entity.setName("bulk-writer-upsert-test");
        entity.setDescription("Old description");
        entity.setOwnerName("spring-owner");
        entity.setLanguage("Java");
        entity.setStars(100);
        entity.setForks(20);
        entity.setHtmlUrl("https://github.com/test/spring-boot");
        entity.setCreatedAt(now);
        entity.setLastUpdated(now);
        entity.setModifiedAt(now);

        // First call: insert repository
        bulkWriter.upsertAll(List.of(entity));

        // Change repository details
        entity.setDescription("Updated description");
        entity.setStars(500);
        entity.setForks(100);

        // Second call: update existing repository
        bulkWriter.upsertAll(List.of(entity));

        GithubRepositoryEntity savedRepository =
                repository.findById(888888L).orElseThrow();

        assertEquals("Updated description", savedRepository.getDescription());
        assertEquals(500, savedRepository.getStars());
        assertEquals(100, savedRepository.getForks());

        // Verify that only one record exists
        assertEquals(
                1,
                repository.findAll().stream()
                        .filter(repo -> repo.getId().equals(888888L))
                        .count()
        );
    }

    @Test
    void shouldInsertMultipleRepositories() {

        GithubRepositoryEntity first = createRepository(
                888887L, "bulk-insert-test-one"
        );

        GithubRepositoryEntity second = createRepository(
                888886L, "bulk-insert-test-two"
        );

        bulkWriter.upsertAll(List.of(first, second));

        GithubRepositoryEntity savedFirst =
                repository.findById(888887L).orElseThrow();

        GithubRepositoryEntity savedSecond =
                repository.findById(888886L).orElseThrow();

        assertEquals("bulk-insert-test-one", savedFirst.getName());
        assertEquals("bulk-insert-test-two", savedSecond.getName());

        assertEquals(2, repository.findAll().stream()
                .filter(repo -> List.of(888887L, 888886L)
                        .contains(repo.getId()))
                .count());
    }

    private GithubRepositoryEntity createRepository(Long id, String name) {

        OffsetDateTime now = OffsetDateTime.now();

        GithubRepositoryEntity entity = new GithubRepositoryEntity();

        entity.setId(id);
        entity.setName(name);
        entity.setDescription("Bulk writer integration test");
        entity.setOwnerName("test-owner");
        entity.setLanguage("BulkWriterTestLanguage");
        entity.setStars(100);
        entity.setForks(20);
        entity.setHtmlUrl("https://github.com/test/" + name);
        entity.setCreatedAt(now);
        entity.setLastUpdated(now);
        entity.setModifiedAt(now);

        return entity;
    }

    @Test
    void shouldHandleEmptyListWithoutInsertingRecords() {

        long countBefore = repository.count();

        bulkWriter.upsertAll(List.of());

        assertEquals(countBefore, repository.count());
    }

    @Test
    void shouldHandleNullInputWithoutThrowingException() {

        long countBefore = repository.count();

        bulkWriter.upsertAll(null);

        assertEquals(countBefore, repository.count());
    }

    @Test
    void shouldPreserveCreatedAtWhenUpdatingExistingRepository() {

        GithubRepositoryEntity entity = createRepository(
                888885L, "created-at-test"
        );

        OffsetDateTime originalCreatedAt =
                OffsetDateTime.parse("2020-01-01T10:00:00Z");

        entity.setCreatedAt(originalCreatedAt);

        bulkWriter.upsertAll(List.of(entity));

        entity.setCreatedAt(
                OffsetDateTime.parse("2022-01-01T10:00:00Z")
        );
        entity.setStars(900);

        bulkWriter.upsertAll(List.of(entity));

        GithubRepositoryEntity savedRepository =
                repository.findById(888885L).orElseThrow();

        assertEquals(originalCreatedAt, savedRepository.getCreatedAt());
        assertEquals(900, savedRepository.getStars());
    }

    @Test
    void shouldInsertNewAndUpdateExistingRepositoriesInSameOperation() {

        // Create an existing repository
        GithubRepositoryEntity existingRepository =
                createRepository(888884L, "existing-repository");

        bulkWriter.upsertAll(List.of(existingRepository));

        // Update existing repository
        existingRepository.setStars(1000);
        existingRepository.setDescription("Updated repository");

        // Create a new repository
        GithubRepositoryEntity newRepository =
                createRepository(888883L, "new-repository");

        // Both operations in one call
        bulkWriter.upsertAll(
                List.of(existingRepository, newRepository)
        );

        GithubRepositoryEntity updated =
                repository.findById(888884L).orElseThrow();

        GithubRepositoryEntity inserted =
                repository.findById(888883L).orElseThrow();

        assertEquals(1000, updated.getStars());
        assertEquals("Updated repository", updated.getDescription());

        assertEquals("new-repository", inserted.getName());

        assertEquals(2, repository.findAll().stream()
                .filter(repo -> List.of(888884L, 888883L)
                        .contains(repo.getId()))
                .count());
    }

    @Test
    void shouldReturnRepositoriesWhenOptionalFiltersAreNull() {

        GithubRepositoryEntity javaRepository =
                createRepository(999992L, "Java");

        GithubRepositoryEntity pythonRepository =
                createRepository(999991L, "Python");

        repository.saveAllAndFlush(
                List.of(javaRepository, pythonRepository)
        );

        List<GithubRepositoryEntity> result =
                repository.findByFilters(
                        null,
                        null,
                        Sort.by(Sort.Direction.DESC, "stars")
                );

        List<Long> resultIds = result.stream()
                .map(GithubRepositoryEntity::getId)
                .toList();

        assertEquals(true, resultIds.contains(999992L));
        assertEquals(true, resultIds.contains(999991L));
    }
}
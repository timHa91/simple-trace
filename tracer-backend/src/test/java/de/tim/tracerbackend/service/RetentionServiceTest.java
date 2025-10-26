package de.tim.tracerbackend.service;

import de.tim.tracerbackend.SpanTestBuilder;
import de.tim.tracerbackend.model.Span;
import de.tim.tracerbackend.repository.SpanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class RetentionServiceTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private SpanRepository spanRepository;

    @Autowired
    private RetentionService retentionService;

    @BeforeEach
    void setUp() {
        spanRepository.deleteAll();
    }

    @Test
    void shouldDeleteOldUnpinnedSpans() {
        // Given: Old unpinned Span
        Span oldSpan = new SpanTestBuilder()
                .id("old-span")
                .timestamp(Instant.now().minus(10, ChronoUnit.DAYS))
                .build();
        spanRepository.save(oldSpan);

        // When
        long deletedCount = retentionService.runNightlyRetention();

        // Then: Span is deleted
        assertThat(deletedCount).isEqualTo(1);
        assertThat(spanRepository.findById("old-span")).isEmpty();
    }

    @Test
    void shouldNotDeletePinnedSpans() {
        // Given: Old pinned Span
        Span pinnedSpan = new SpanTestBuilder()
                .id("pinned-span")
                .timestamp(Instant.now().minus(10, ChronoUnit.DAYS))
                .build();
        pinnedSpan.markAsPinned();
        spanRepository.save(pinnedSpan);

        // When:
        retentionService.runNightlyRetention();

        // Then: Span should not be deleted
        assertThat(spanRepository.findById("pinned-span")).isPresent();
    }

    @Test
    void shouldNotDeleteRecentSpans() {
        // Given: New Span (3 Days old)
        Span recentSpan = new SpanTestBuilder()
                .id("recent-span")
                .timestamp(Instant.now().minus(3, ChronoUnit.DAYS))
                .build();
        spanRepository.save(recentSpan);

        // When
        retentionService.runNightlyRetention();

        // Then: Span should not be deleted
        assertThat(spanRepository.findById("recent-span")).isPresent();
    }
}
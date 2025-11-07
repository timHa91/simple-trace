package de.tim.tracerbackend.controller;

import de.tim.tracerbackend.SpanTestBuilder;
import de.tim.tracerbackend.TraceDtoTestBuilder;
import de.tim.tracerbackend.dto.ErrorResponse;
import de.tim.tracerbackend.dto.TraceDto;
import de.tim.tracerbackend.dto.TraceSummaryDto;
import de.tim.tracerbackend.model.Span;
import de.tim.tracerbackend.repository.SpanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TraceControllerIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SpanRepository spanRepository;

    @BeforeEach
    void setUp() {
        spanRepository.deleteAll();
        spanRepository.saveAll(createAllTestSpans());
    }

    @Test
    void shouldStoreAndRetrieveTrace() {
        // Given: send Span
        String testTraceId = "trace-id";
        TraceDto span = new TraceDtoTestBuilder()
                .spanId("span-1")
                .parentSpanId(null) // Root Span
                .serviceName("Service-A")
                .traceId(testTraceId)
                .type("inbound")
                .build();
        restTemplate.postForEntity("/api/traces", span, Void.class);
        var savedSpan = spanRepository.findById("span-1");
        assertNotNull(savedSpan, "Saved span is null");

        // When: request Trace
        String tree = restTemplate.getForObject("/api/traces/{id}/tree",
                String.class, testTraceId);
        
        // Then
        assertNotNull(tree, "Requested tree is null");
        assertThat(tree).contains("Service-A [inbound]");
    }

    @Test
    void shouldGetAllTraces() {
        // When:
        ResponseEntity<List<TraceSummaryDto>> response = restTemplate.exchange(
                "/api/traces",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TraceSummaryDto>>() {}
        );

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        List<TraceSummaryDto> result = response.getBody();
        assertNotNull(result, "Requested traces are null");
        assertThat(result.size()).isEqualTo(3);

        assertThat(result)
                .extracting(TraceSummaryDto::traceId)
                .containsExactlyInAnyOrder("trace-1", "trace-2", "trace-3");

        assertThat(result)
                .filteredOn(t -> t.traceId().equals("trace-1"))
                .first()
                .satisfies(t -> assertThat(t.overallStatus() == 200))
                .satisfies(t -> assertThat(t.services().containsAll(List.of("service-a", "service-b", "service-c"))))
                .satisfies(t -> assertThat(t.totalDuration() == 100L))
                .satisfies(t -> assertThat(t.spanCount() == 3)); // 3 Spans 1 Orphan
    }

    @Test
    void shouldGetAllTracesSortedDesc() {
        // When: SortBy SPAN_COUNT
        ResponseEntity<List<TraceSummaryDto>> response = restTemplate.exchange(
                "/api/traces?sortBy=span_count",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TraceSummaryDto>>() {}
        );

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<TraceSummaryDto> result = response.getBody();
        assertNotNull(result, "Requested traces are null");
        assertThat(result.size()).isEqualTo(3);

        assertThat(result).isSortedAccordingTo(Comparator.comparing(TraceSummaryDto::spanCount).reversed());
    }

    @Test
    void shouldGetAllTracesSortedAsc() {
        // When: Default SortBy && SortOrder ASC
        ResponseEntity<List<TraceSummaryDto>> response = restTemplate.exchange(
                "/api/traces?sortOrder=asc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TraceSummaryDto>>() {}
        );

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<TraceSummaryDto> result = response.getBody();
        assertNotNull(result, "Requested traces are null");
        assertThat(result.size()).isEqualTo(3);

        assertThat(result).isSortedAccordingTo(Comparator.comparing(TraceSummaryDto::totalDuration));
    }

    @Test
    void shouldGetAllTracesWithSingleFilter() {
        // When: Default Sort && Filtered by service-b
        ResponseEntity<List<TraceSummaryDto>> response = restTemplate.exchange(
                "/api/traces?serviceName=service-b",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TraceSummaryDto>>() {}
        );

        // Then: One Trace with service-b
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<TraceSummaryDto> result = response.getBody();
        assertNotNull(result, "Requested trace is null");
        assertThat(result.size()).isEqualTo(1);

        assertThat(result.getFirst().services()).satisfies(s -> assertThat(s.contains("service-b")));
    }

    @Test
    void shouldGetAllTracesWithMultipleFilters() {
        // When: Filtered by status && serviceName
        ResponseEntity<List<TraceSummaryDto>> response = restTemplate.exchange(
                "/api/traces?status=400&serviceName=service-g",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TraceSummaryDto>>() {}
        );

        // Then: One Trace with status 400 and service-g
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<TraceSummaryDto> result = response.getBody();
        assertNotNull(result, "Requested traces are null");
        assertThat(result.size()).isEqualTo(1);

        assertThat(result.getFirst().services())
                .contains("service-g")
                .doesNotContain("service-a");
        assertThat(result.getFirst().overallStatus()).isEqualTo(400);
    }

    @Test
    void shouldGetAllErrorTraces() {
        // When: Only Error Traces
        ResponseEntity<List<TraceSummaryDto>> response = restTemplate.exchange(
                "/api/traces/errors",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TraceSummaryDto>>() {}
        );

        // Then: One Trace with status 400
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<TraceSummaryDto> result = response.getBody();
        assertNotNull(result, "Requested error traces are null");

        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .filteredOn(t -> t.traceId().equals("trace-2"))
                .first()
                .satisfies(t -> assertThat(t.services().containsAll(List.of("service-e", "service-f", "service-g", "service-h"))))
                .satisfies(t -> assertThat(t.overallStatus() == 400));

        assertThat(result)
                .filteredOn(t -> t.traceId().equals("trace-3"))
                .first()
                .satisfies(t -> assertThat(t.services().contains("service-g")))
                .satisfies(t -> assertThat(t.overallStatus() == 500));

    }

    @Test
    void shouldGetAllErrorTracesFiltered() {
        // When: Should only one Error Trace
        ResponseEntity<List<TraceSummaryDto>> response = restTemplate.exchange(
                "/api/traces/errors?serviceName=service-e",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TraceSummaryDto>>() {}
        );

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<TraceSummaryDto> result = response.getBody();
        assertNotNull(result, "Requested error traces are null");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().services().containsAll(List.of("service-e", "service-f", "service-g", "service-h")));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidInputSortBy() {
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/traces?sortBy=invalid",
                HttpMethod.GET,
                null,
                ErrorResponse.class
        );

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        ErrorResponse result = response.getBody();

        assertNotNull(result, "Error response should not be null");
        assertThat(result.error()).isEqualTo("Invalid Parameter");
        assertThat(result.message()).isEqualTo("Invalid value for parameter 'sortBy'. Expected type: SortField");
        assertThat(result.path()).isEqualTo("/api/traces");
        assertThat(result.status()).isEqualTo(400);
    }

    @Test
    void shouldReturnBadRequest_whenInvalidInputSortOrder() {
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/traces?sortOrder=abc",
                HttpMethod.GET,
                null,
                ErrorResponse.class
        );

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        ErrorResponse result = response.getBody();

        assertNotNull(result, "Error response should not be null");
        assertThat(result.error()).isEqualTo("Invalid Parameter");
        assertThat(result.message()).isEqualTo("Invalid value for parameter 'sortOrder'. Expected type: SortOrder");
        assertThat(result.path()).isEqualTo("/api/traces");
        assertThat(result.status()).isEqualTo(400);
    }

    @Test
    void shouldReturnEmptyList_whenNoTracesFoundForFilterValue() {
        // When
        ResponseEntity<List<TraceSummaryDto>> response = restTemplate.exchange(
                "/api/traces?serviceName=not-existing-service",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TraceSummaryDto>>() {}
        );

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEmpty();
    }


    private List<Span> createAllTestSpans() {
        var spans = new ArrayList<Span>();

        // First Trace Spans with Orphan
        var span1Trace1 = new SpanTestBuilder()
                .id("span-1-1")
                .traceId("trace-1")
                .parentId(null)
                .serviceName("service-a")
                .duration(100L)
                .type("inbound")
                .build();
        spans.add(span1Trace1);

        var span2Trace1 = new SpanTestBuilder()
                .id("span-2-1")
                .traceId("trace-1")
                .parentId("span-1-1")
                .serviceName("service-b")
                .type("inbound")
                .build();
        spans.add(span2Trace1);

        var span3Trace1 = new SpanTestBuilder()
                .id("span-3-1")
                .traceId("trace-1")
                .parentId("span-2-1")
                .serviceName("service-c")
                .type("outbound")
                .build();
        spans.add(span3Trace1);

        var orphanSpanTrace1 = new SpanTestBuilder()
                .id("span-4-1")
                .traceId("trace-1")
                .parentId(null)
                .serviceName("service-d")
                .type("outbound")
                .build();
        spans.add(orphanSpanTrace1);

        // Second Trace with Error
        var span1Trace2 = new SpanTestBuilder()
                .id("span-1-2")
                .traceId("trace-2")
                .parentId(null)
                .serviceName("service-e")
                .duration(300L)
                .type("inbound")
                .build();
        spans.add(span1Trace2);

        var span2Trace2 = new SpanTestBuilder()
                .id("span-2-2")
                .traceId("trace-2")
                .parentId("span-1-2")
                .serviceName("service-f")
                .type("inbound")
                .build();
        spans.add(span2Trace2);

        var errorSpanTrace2 = new SpanTestBuilder()
                .id("span-3-2")
                .traceId("trace-2")
                .parentId("span-2-2")
                .serviceName("service-g")
                .status(400)
                .type("outbound")
                .build();
        spans.add(errorSpanTrace2);

        var span4Trace2 = new SpanTestBuilder()
                .id("span-4-2")
                .traceId("trace-2")
                .parentId("span-2-2")
                .serviceName("service-h")
                .status(200)
                .type("outbound")
                .build();
        spans.add(span4Trace2);

        // Third Trace with 1 Error-Span
        var span1Trac3 = new SpanTestBuilder()
                .id("span-1-3")
                .traceId("trace-3")
                .parentId(null)
                .status(500)
                .serviceName("service-g")
                .duration(500L)
                .type("inbound")
                .build();
        spans.add(span1Trac3);

        return spans;
    }

}
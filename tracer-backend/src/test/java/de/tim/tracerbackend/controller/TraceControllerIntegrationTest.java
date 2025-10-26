package de.tim.tracerbackend.controller;

import de.tim.tracerbackend.dto.TraceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TraceControllerIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;

    private static final String TEST_TRACE_ID = "trace-id";
    
    @Test
    void shouldStoreAndRetrieveTrace() {
        // Given: Span senden
        TraceDto span = createTestTraceDto();
        restTemplate.postForEntity("/api/traces", span, Void.class);
        
        // When: Trace abrufen
        String tree = restTemplate.getForObject("/api/traces/{id}/tree",
                String.class, TEST_TRACE_ID);
        
        // Then
        assertThat(tree).contains("Service-A [inbound]");
    }

    private TraceDto createTestTraceDto() {
        return new TraceDto(
                "Service-A",
                TEST_TRACE_ID,
                "span-id",
                null,  // Root Span
                "test-operation",
                200,
                Instant.now(),
                1l,
                "error-msg",
                "inbound"
        );
    }
}
package de.tim.tracerbackend.dto;

import java.time.Instant;

public record TraceDto(
        String serviceName,
        String traceId,
        String spanId,
        String parentSpanId,
        String operation,
        Integer status,
        Instant timestamp,
        long duration,
        String errorMessage,
        String type
) {
}

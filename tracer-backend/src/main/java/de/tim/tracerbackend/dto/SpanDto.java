package de.tim.tracerbackend.dto;

import java.time.Instant;
import java.util.List;

public record SpanDto(
        String serviceName,
        String operation,
        Integer status,
        Instant timestamp,
        long duration,
        String errorMessage,
        String type,
        List<SpanDto> children
) {
}

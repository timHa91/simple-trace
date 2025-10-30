package de.tim.tracerbackend.dto;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        String error,
        String message,
        int status,
        String path
) {
}

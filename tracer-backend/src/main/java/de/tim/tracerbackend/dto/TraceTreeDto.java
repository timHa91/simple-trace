package de.tim.tracerbackend.dto;

import java.util.List;

public record TraceTreeDto(
        String traceId,
        SpanDto rootSpan,
        List<SpanDto> orphans
) {
}

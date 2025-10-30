package de.tim.tracerbackend.dto;

import java.util.List;

public record TraceSummaryDto(
        String traceId,
        Long totalDuration,
        List<String> services,
        int spanCount,
        int overallStatus
) {
}

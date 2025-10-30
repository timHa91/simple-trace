package de.tim.tracerbackend.specification;

import de.tim.tracerbackend.model.TraceSummary;

import java.util.function.Predicate;

public class TraceFilterSpecification {
    private static final int ERROR_STATUS_THRESHOLD = 400;

    private final String serviceName;
    private final Predicate<TraceSummary> statusFilter;
    private final Long minDuration;

    private TraceFilterSpecification(String serviceName, Predicate<TraceSummary> statusFilter, Long minDuration) {
        this.serviceName = serviceName;
        this.statusFilter = statusFilter;
        this.minDuration = minDuration;
    }

    public static TraceFilterSpecification create(
            String serviceName,
            Integer exactStatus,
            Long minDuration
    ) {
        Predicate<TraceSummary> statusFilter = exactStatus != null
                ? summary -> summary.getOverallStatus() == exactStatus
                : summary -> true;

        return new TraceFilterSpecification(
                serviceName,
                statusFilter,
                minDuration
        );
    }

    public static TraceFilterSpecification createError(
            String serviceName,
            Long minDuration
    ) {
        Predicate<TraceSummary> statusFilter = summary -> summary.getOverallStatus() >= ERROR_STATUS_THRESHOLD;

        return new TraceFilterSpecification(
                serviceName,
                statusFilter,
                minDuration
        );
    }

    public boolean matches(TraceSummary summary) {
        // Service filter
        if (serviceName != null && !summary.getServices().contains(serviceName)) {
            return false;
        }

        // Duration filter
        if (minDuration != null && summary.getTotalDuration() < minDuration) {
            return false;
        }

        // Status filter
        return statusFilter.test(summary);
    }
}

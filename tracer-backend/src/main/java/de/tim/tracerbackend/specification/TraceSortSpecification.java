package de.tim.tracerbackend.specification;

import de.tim.tracerbackend.dto.SortField;
import de.tim.tracerbackend.dto.SortOrder;
import de.tim.tracerbackend.model.TraceSummary;

import java.util.Comparator;

public class TraceSortSpecification {
    private final Comparator<TraceSummary> comparator;

    private TraceSortSpecification(Comparator<TraceSummary> comparator) {
        this.comparator = comparator;
    }

    public static TraceSortSpecification create(SortField sortField, SortOrder sortOrder) {
        Comparator<TraceSummary> comp = switch (sortField) {
            case DURATION -> Comparator.comparing(
                    TraceSummary::getTotalDuration,
                    Comparator.nullsLast(Long::compareTo)
            );
            case SPAN_COUNT -> Comparator.comparing(TraceSummary::getSpanCount);
            case STATUS -> Comparator.comparing(TraceSummary::getOverallStatus);
        };

        return new TraceSortSpecification(
                sortOrder == SortOrder.DESC ? comp.reversed() : comp
        );
    }

    public Comparator<TraceSummary> getComparator() {
        return comparator;
    }
}

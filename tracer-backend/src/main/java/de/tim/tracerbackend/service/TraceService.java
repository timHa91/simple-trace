package de.tim.tracerbackend.service;

import de.tim.tracerbackend.specification.TraceFilterSpecification;
import de.tim.tracerbackend.specification.TraceSortSpecification;
import de.tim.tracerbackend.dto.TraceDto;
import de.tim.tracerbackend.model.Span;
import de.tim.tracerbackend.model.TraceSummary;
import de.tim.tracerbackend.model.TraceTree;
import de.tim.tracerbackend.repository.SpanRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TraceService {

    private final SpanRepository spanRepository;
    private final MetricsService metricsService;

    public TraceService(SpanRepository spanRepository, MetricsService metricsService) {
        this.spanRepository = spanRepository;
        this.metricsService = metricsService;
    }

    public void addTrace(TraceDto traceDto) {
        var span = new Span(
                traceDto.spanId(),
                traceDto.traceId(),
                traceDto.parentSpanId(),
                traceDto.serviceName(),
                traceDto.operation(),
                traceDto.status(),
                traceDto.timestamp(),
                traceDto.duration(),
                traceDto.errorMessage(),
                traceDto.type()
        );
        spanRepository.save(span);

        metricsService.recordSpanReceived(traceDto.serviceName());
        metricsService.recordSpanDuration(traceDto.serviceName(), traceDto.duration());
        boolean isErrorStatus = traceDto.status() != null && (traceDto.status() >= 400 || traceDto.status() < 0);
        if (isErrorStatus) {
            metricsService.recordSpanError(traceDto.serviceName());
        }
    }

    public Optional<TraceTree> findTrace(String traceId) {
        var spans =  spanRepository.findByTraceId(traceId);
        if (spans.isEmpty()) return Optional.empty();

        var traceTree = TraceTree.build(traceId, spans);
        traceTree.getOrphanSpans().forEach(o -> metricsService.recordOrphanedSpan(o.getServiceName()));

        return Optional.of(traceTree);
    }

    public List<TraceSummary> findAllTracesAsSummary(TraceFilterSpecification filterSpec, TraceSortSpecification sortSpec) {
        var spans = spanRepository.findAll();
        var traceIdToSummaryMap = createTraceIdToSummaryMap(spans);

        return traceIdToSummaryMap.values().stream()
                .filter(filterSpec::matches)
                .sorted(sortSpec.getComparator())
                .toList();
    }

    private Map<String, TraceSummary> createTraceIdToSummaryMap(List<Span> spans) {
        var traceIdToSummaryMap = new HashMap<String, TraceSummary>();

        for (var span : spans) {
            TraceSummary summary = traceIdToSummaryMap.computeIfAbsent(
                    span.getTraceId(),
                    id -> new TraceSummary(
                            span.getTraceId(),
                            null,
                            span.getStatus()
                    )
            );

            summary.incrementCount();
            summary.addService(span.getServiceName());
            summary.updateOverAllStatus(span.getStatus());

            // Total duration deduce from root span
            if (span.getParentId() == null) {
                summary.setTotalDuration(span.getDuration());
            }
        }
        return traceIdToSummaryMap;
    }
}

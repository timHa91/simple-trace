package de.tim.tracerbackend.service;

import de.tim.tracerbackend.model.Span;
import de.tim.tracerbackend.model.TraceTree;
import de.tim.tracerbackend.dto.TraceDto;
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
        if (traceDto.status() != null && (traceDto.status() >= 400 || traceDto.status() < 0)) {
            metricsService.recordSpanError(traceDto.serviceName());
        }
    }

    public Optional<TraceTree> findTrace(String traceId) {
        var spans =  spanRepository.findByTraceId(traceId);

        if (spans.isEmpty()) return Optional.empty();

        var traceTree = TraceTree.build(traceId, spans);;

        traceTree.getOrphanSpans().forEach(o -> metricsService.recordOrphanedSpan(o.getServiceName()));

        return Optional.of(traceTree);
    }

}

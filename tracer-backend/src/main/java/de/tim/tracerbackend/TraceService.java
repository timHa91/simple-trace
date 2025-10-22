package de.tim.tracerbackend;

import de.tim.tracerbackend.dto.TraceDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TraceService {

    private final TraceCache cache;
    private final MetricsService metricsService;

    public TraceService(TraceCache cache, MetricsService metricsService) {
        this.cache = cache;
        this.metricsService = metricsService;
    }

    public void addTrace(TraceDto traceDto) {
        cache.put(traceDto);

        metricsService.recordSpanReceived(traceDto.serviceName());
        metricsService.recordSpanDuration(traceDto.serviceName(), traceDto.duration());

        if (traceDto.status() != null && (traceDto.status() >= 400 || traceDto.status() < 0)) {
            metricsService.recordSpanError(traceDto.serviceName());
        }

    }

    public Optional<TraceTree> findTrace(String traceId) {
        TraceTree traceTree =  cache.get(traceId);
        if (traceTree == null) return Optional.empty();

        traceTree.buildTree();

       traceTree.getOrphanSpans().forEach(o -> metricsService.recordOrphanedSpan(o.getServiceName()));

        return Optional.of(traceTree);
    }

}

package de.tim.tracerbackend;

import de.tim.tracerbackend.dto.TraceDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TraceCache {

    private static final int MAX_ENTRIES = 1000;
    private final Map<String, TraceTree> tracesByTraceId = new HashMap<>();

    public synchronized void put(TraceDto traceDto) {
        if (tracesByTraceId.size() >= MAX_ENTRIES) {
            System.out.println("Map reset — zu viele Einträge!");
            tracesByTraceId.clear();
        }

        tracesByTraceId.computeIfAbsent(traceDto.traceId(), k -> new TraceTree(traceDto.traceId()))
                .add(
                        new Span(
                                traceDto.spanId(),
                                traceDto.parentSpanId(),
                                traceDto.serviceName(),
                                traceDto.operation(),
                                traceDto.status(),
                                traceDto.timestamp(),
                                traceDto.duration(),
                                traceDto.errorMessage(),
                                traceDto.type()
                        )
                );

    }

    public TraceTree get(String traceId) {
        return tracesByTraceId.get(traceId);
    }

}

package de.tim.tracerbackend.kafka;

import de.tim.tracerbackend.dto.TraceDto;
import de.tim.tracerbackend.service.TraceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SpanListener {

    private final TraceService traceService;

    public SpanListener(TraceService traceService) {
        this.traceService = traceService;
    }

    @KafkaListener(topics = "span-events", groupId = "tracer-service")
    public void listen(TraceDto traceDto) {
        traceService.addTrace(traceDto);
    }
}

package de.tim.tracea;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SpanEventProducer {

    private final KafkaTemplate<String, TraceDto> kafkaTemplate;

    public SpanEventProducer(KafkaTemplate<String, TraceDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(TraceDto traceDto) {
        kafkaTemplate.send("span-events", traceDto.getTraceId(), traceDto);
    }
}

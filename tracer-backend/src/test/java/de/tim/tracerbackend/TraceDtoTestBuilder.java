package de.tim.tracerbackend;

import de.tim.tracerbackend.dto.TraceDto;

import java.time.Instant;

public class TraceDtoTestBuilder {
    private String serviceName = "Test-Service";
    private String traceId = "Test-Trace-Id";
    private String spanId = "Test-Span-Id";
    private String parentSpanId = "Test-Parent-Span-Id";
    private String operation = "Test-Operation";
    private int status = 200;
    private Instant timestamp = Instant.now();
    private long duration = 1L;
    private String errorMessage = "Test-Error-Message";
    private String type = "Test-Type";


    public TraceDto build() {

        return new TraceDto(
                serviceName,
                traceId,
                spanId,
                parentSpanId,
                operation,
                status,
                timestamp,
                duration,
                errorMessage,
                type
        );
    }

    public TraceDtoTestBuilder serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public TraceDtoTestBuilder traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public TraceDtoTestBuilder spanId(String spanId) {
        this.spanId = spanId;
        return this;
    }

    public TraceDtoTestBuilder parentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
        return this;
    }

    public TraceDtoTestBuilder operation(String operation) {
        this.operation = operation;
        return this;
    }

    public TraceDtoTestBuilder status(int status) {
        this.status = status;
        return this;
    }

    public TraceDtoTestBuilder timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public TraceDtoTestBuilder duration(long duration) {
        this.duration = duration;
        return this;
    }

    public TraceDtoTestBuilder errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public TraceDtoTestBuilder type(String type) {
        this.type = type;
        return this;
    }
}

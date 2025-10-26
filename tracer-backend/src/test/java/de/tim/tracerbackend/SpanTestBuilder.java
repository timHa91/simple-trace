package de.tim.tracerbackend;

import de.tim.tracerbackend.model.Span;

import java.time.Instant;

public class SpanTestBuilder {

    private String id = "test-id";
    private String traceId = "test-trace-id";
    private String parentId = "test-parent-id";
    private String serviceName = "test-service-name";
    private String operation = "test-operation";
    private int statusCode = 200;
    private Instant timestamp = Instant.now();
    private long duration = 1L;
    private String errorMsg = "test-error-msg";
    private String type = "test-type";

    public SpanTestBuilder id(String id) {
        this.id = id;
        return this;
    }

    public SpanTestBuilder traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public SpanTestBuilder timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public SpanTestBuilder operation(String operation) {
        this.operation = operation;
        return this;
    }

    public SpanTestBuilder parentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public SpanTestBuilder serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public SpanTestBuilder type(String type) {
        this.type = type;
        return this;
    }

    public Span build() {
        return new Span(
                id,
                traceId,
                parentId,
                serviceName,
                operation,
                statusCode,
                timestamp,
                duration,
                errorMsg,
                type
        );
    }
}

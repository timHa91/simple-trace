package de.tim.tracec;

import java.time.Instant;
import java.util.Objects;

public class TraceDto {
    private final String serviceName;
    private final String traceId;
    private final String spanId;
    private final String parentSpanId;
    private final String operation;
    private final int status;
    private final Instant timestamp;
    private final long duration;
    private final String errorMessage;
    private final String type;

    public TraceDto(
            String serviceName,
            String traceId,
            String spanId,
            String parentSpanId,
            String operation,
            int status,
            Instant timestamp,
            long duration,
            String type,
            String errorMessage) {
        this.serviceName = Objects.requireNonNull(serviceName, "ServiceName not nulL");
        this.traceId = Objects.requireNonNull(traceId, "TraceId not null");
        this.spanId = Objects.requireNonNull(spanId, "SpanId not null");
        this.parentSpanId = parentSpanId;
        this.operation = Objects.requireNonNull(operation);
        this.status = status;
        this.timestamp = Objects.requireNonNull(timestamp);
        this.duration = duration;
        this.type = Objects.requireNonNull(type, "Type not null");
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "TraceDto{" +
                "serviceName='" + serviceName + '\'' +
                ", traceId='" + traceId + '\'' +
                ", spanId='" + spanId + '\'' +
                ", parentSpanId='" + parentSpanId + '\'' +
                ", operation='" + operation + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                ", errorMessage='" + errorMessage + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public String getOperation() {
        return operation;
    }

    public int getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getType() {
        return type;
    }

    public String getServiceName() {
        return serviceName;
    }
}

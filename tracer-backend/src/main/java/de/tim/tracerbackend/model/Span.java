package de.tim.tracerbackend.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "spans", indexes = {
        @Index(name = "idx_trace_id",  columnList = "traceId"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class Span {

    @Id
    private String id;

    @Column(name = "trace_id", nullable = false, updatable = false)
    private String traceId;

    @Column(name = "parent_id", updatable = false)
    private String parentId;

    @Column(name = "service_name", nullable = false, updatable = false)
    private String serviceName;

    @Column(name = "operation", nullable = false, updatable = false)
    private String operation;

    @Column(name = "status", nullable = false, updatable = false)
    private Integer status;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    @Column(name = "duration", nullable = false, updatable = false)
    private long duration;

    @Column(name = "error_message", updatable = false)
    private String errorMessage;

    @Column(name = "type", nullable = false, updatable = false)
    private String type;

    @Column(name = "pinned")
    private boolean pinned;

    @Transient
    private List<Span> children = new ArrayList<>();

    public Span(
            String id,
            String traceId,
            String parentId,
            String serviceName,
            String operation,
            Integer status,
            Instant timestamp,
            long duration,
            String errorMessage,
            String type) {
        this.id = id;
        this.traceId = traceId;
        this.parentId = parentId;
        this.serviceName = serviceName;
        this.operation = operation;
        this.status = status;
        this.timestamp = timestamp;
        this.duration = duration;
        this.errorMessage = errorMessage;
        this.type = type;
        this.pinned = false;
    }

    protected Span() {}

    public void setChildren(List<Span> children) {
        if (children == null || children.isEmpty()) {
            return;
        }

        this.children = children;
    }

    public void markAsPinned() {
        this.pinned = true;
    }

    @Override
    public String toString() {
        return  serviceName + " [" + type + "] " + operation + " (" + duration + "ms)";
    }

    public String getId() {
        return id;
    }
    public String getTraceId() { return traceId; }
    public String getParentId() {
        return parentId;
    }
    public String getServiceName() {
        return serviceName;
    }
    public String getOperation() {
        return operation;
    }
    public Integer getStatus() {
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
    public List<Span> getChildren() {
        return children;
    }
    public boolean isPinned() {return pinned;}
}

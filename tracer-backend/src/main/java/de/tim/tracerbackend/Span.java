package de.tim.tracerbackend;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Span {

    private final String id;
    private final String parentId;
    private final String serviceName;
    private final String operation;
    private final Integer status;
    private final Instant timestamp;
    private final long duration;
    private final String errorMessage;
    private final String type;

    private List<Span> children = new ArrayList<>();

    public Span(
            String id,
            String parentId,
            String serviceName,
            String operation,
            Integer status,
            Instant timestamp,
            long duration,
            String errorMessage,
            String type) {
        this.id = id;
        this.parentId = parentId;
        this.serviceName = serviceName;
        this.operation = operation;
        this.status = status;
        this.timestamp = timestamp;
        this.duration = duration;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    public void setChildren(List<Span> children) {
        if (children == null || children.isEmpty()) {
            return;
        }

        this.children = children;
    }

    @Override
    public String toString() {
        return  serviceName + " [" + type + "] " + operation + " (" + duration + "ms)";
    }

    public String getId() {
        return id;
    }

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
}

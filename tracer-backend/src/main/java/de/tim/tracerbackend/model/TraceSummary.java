package de.tim.tracerbackend.model;

import java.util.*;

public class TraceSummary {
    private final String traceId;
    private Long totalDuration;
    private final Set<String> services;
    private int spanCount;
    private int overallStatus;

    public TraceSummary(String traceId, Long totalDuration, int overallStatus) {
        this.traceId = traceId;
        this.totalDuration = totalDuration;
        this.services = new HashSet<>();
        this.spanCount = 0;
        this.overallStatus = overallStatus;
    }

    public void incrementCount() {
        this.spanCount ++;
    }

    public void addService(String service) {
        this.services.add(service);
    }

    public void updateOverAllStatus(int status) {
        if (status != 200) {
            this.overallStatus = status;
        }
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Long getTotalDuration() {
        return totalDuration;
    }

    public List<String> getServices() {
        return services.stream().sorted().toList();
    }

    public int getSpanCount() {
        return spanCount;
    }

    public int getOverallStatus() {
        return overallStatus;
    }
}

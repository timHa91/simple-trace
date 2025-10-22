package de.tim.tracerbackend.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordSpanReceived(String serviceName) {
        meterRegistry.counter("spans.received", "service", serviceName).increment();
    }

    public void recordSpanDuration(String serviceName, long duration) {
        meterRegistry.timer("spans.duration", "service", serviceName)
                .record(duration, TimeUnit.MILLISECONDS);
    }

    public void recordSpanError(String serviceName) {
        meterRegistry.counter("spans.error", "service", serviceName).increment();
    }

    public void recordOrphanedSpan(String serviceName) {
        meterRegistry.counter("spans.orphaned", "service", serviceName).increment();
    }

}

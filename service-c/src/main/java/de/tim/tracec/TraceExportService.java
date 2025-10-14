package de.tim.tracec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TraceExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceExportService.class);

    @Value("${tracer.backend.url}")
    private String url;

    private final RestTemplate restTemplate;

    public TraceExportService(@Qualifier("traceExport") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void export(TraceDto trace) {
        if (trace == null) {
            LOGGER.debug("Skipping null trace export");
            return;
        }

        try {
            restTemplate.postForEntity(url, trace, TraceDto.class);
            LOGGER.debug("Successfully exported trace [traceId={}, spanId={}]",
                    trace.getTraceId(), trace.getSpanId());
        } catch (Exception e) {
            LOGGER.warn("Failed to export trace to backend [url={}, traceId={}, spanId={}]: {}",
                    url, trace.getTraceId(), trace.getSpanId(), e.getMessage(), e);

        }
    }
}

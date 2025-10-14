package de.tim.tracea;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Configuration
public class AppConfig {

    @Value("${service.name}")
    private String serviceName;

    private final TraceExportService exportService;

    public AppConfig(TraceExportService exportService) {
        this.exportService = exportService;
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            String traceId = TraceContext.getTraceId();
            if (traceId != null) {
                request.getHeaders().add("X-trace-id", traceId);
            }

            String parentSpanId = TraceContext.getCurrentSpanId(); // A-inbound
            String spanId = UUID.randomUUID().toString();          // A-outbound
            TraceContext.setCurrentSpanId(spanId);
            request.getHeaders().add("X-parent-span-id", spanId);  // B bekommt A-outbound als parent

            Instant start = Instant.now();
            Integer status = null;
            String errorMessage = null;

            try {
                ClientHttpResponse response = execution.execute(request, body);
                status = response.getStatusCode().value();
                if (status >= 400) {
                    try {
                        errorMessage = new String(
                                response.getBody().readAllBytes(),
                                StandardCharsets.UTF_8
                        );
                    } catch (IOException e) {
                        errorMessage = "Status " + status + " (body unreadable)";
                    }

                }
                return response;
            } catch (IOException ex) {
                status = -1;
                errorMessage = ex.getMessage();
                throw new ResourceAccessException("I/O error", ex);
            } finally {
                long duration = Duration.between(start, Instant.now()).toMillis();

                TraceDto trace = new TraceDto(
                        serviceName,
                        traceId,
                        spanId,
                        parentSpanId,
                        request.getMethod() + " " + request.getURI().getPath(),
                        status,
                        start,
                        duration,
                        "outbound",
                        errorMessage
                );
                exportService.export(trace);

                TraceContext.setCurrentSpanId(parentSpanId);
            }
        });

        return restTemplate;
    }
}

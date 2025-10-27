package de.tim.tracec;

import org.springframework.beans.factory.annotation.Qualifier;
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

    private final SpanEventProducer producer;

    public AppConfig(SpanEventProducer producer) {
        this.producer = producer;
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add(((request, body, execution) -> {
            Instant startTime = Instant.now();

            String traceId = TraceContext.getTraceId();
            if (traceId == null) {
                return execution.execute(request, body);
            }
            request.getHeaders().add("X-trace-id", traceId);

            String previousId = TraceContext.getCurrentSpanId();
            String spanId = UUID.randomUUID().toString();
            TraceContext.setCurrentSpanId(spanId);

            request.getHeaders().add("X-parent-span-id", spanId);

            TraceDto traceDto;
            Integer status = null;
            String errorMessage = null;
            try {
                ClientHttpResponse response = execution.execute(request, body);
                status = response.getStatusCode().value();

                if (status >= 400) {
                    try{
                        errorMessage = new String(
                                response.getBody().readAllBytes(),
                                StandardCharsets.UTF_8
                        );
                    } catch (IOException ex) {
                        errorMessage = "Status " + status + " (body unreadable)";
                    }
                }

                return response;
            } catch (IOException ex) {
                status = -1;
                errorMessage = ex.getMessage();

                throw new ResourceAccessException("I/O error", ex);
            } finally {
                traceDto = new TraceDto(
                        serviceName,
                        traceId,
                        spanId,
                        previousId,
                        request.getMethod() + " " + request.getURI(),
                        status,
                        startTime,
                        Duration.between(startTime, Instant.now()).toMillis(),
                        "outbound",
                        errorMessage
                );
                producer.sendEvent(traceDto);
                TraceContext.setCurrentSpanId(previousId);
            }
        }));

        return restTemplate;
    }

}

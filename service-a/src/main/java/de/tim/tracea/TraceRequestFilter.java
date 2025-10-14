package de.tim.tracea;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class TraceRequestFilter extends OncePerRequestFilter {

    @Value("${service.name}")
    private String serviceName;

    private final TraceExportService traceExportService;

    public TraceRequestFilter(TraceExportService traceExportService) {
        this.traceExportService = traceExportService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        String tracIdFromHeader = request.getHeader("X-trace-id");
        boolean traceIdNotInHeader = tracIdFromHeader == null || tracIdFromHeader.isBlank();
        if (traceIdNotInHeader) {
            TraceContext.createTraceId();
        } else {
            TraceContext.setTraceId(tracIdFromHeader);
        }

        String parentSpanIdFromHeader = request.getHeader("X-parent-span-id");
        String spanId = UUID.randomUUID().toString();
        TraceContext.setCurrentSpanId(spanId);

        try {
            doFilter(request, response, filterChain);
        } finally {
            long endTime = System.currentTimeMillis();

            TraceDto inboundSpan = new TraceDto(
                    serviceName,
                    TraceContext.getTraceId(),
                    spanId,
                    parentSpanIdFromHeader,
                    request.getMethod() + request.getRequestURI(),
                    response.getStatus(),
                    Instant.now(),
                    endTime - startTime,
                    "inbound",
                    null
            );

            traceExportService.export(inboundSpan);
            TraceContext.clearAll();
        }
    }
}

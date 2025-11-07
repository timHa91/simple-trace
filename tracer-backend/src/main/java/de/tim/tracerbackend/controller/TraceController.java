package de.tim.tracerbackend.controller;

import de.tim.tracerbackend.dto.*;
import de.tim.tracerbackend.model.Span;
import de.tim.tracerbackend.model.TraceSummary;
import de.tim.tracerbackend.model.TraceTree;
import de.tim.tracerbackend.service.TraceService;
import de.tim.tracerbackend.specification.TraceFilterSpecification;
import de.tim.tracerbackend.specification.TraceSortSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/traces")
public class TraceController {

    private final TraceService traceService;

    public TraceController(TraceService traceService) {
        this.traceService = traceService;
    }

    @PostMapping
    ResponseEntity<Void> addTrace(@RequestBody TraceDto traceDto) {
        traceService.addTrace(traceDto);

        return ResponseEntity.accepted().build();
    }
    
    @GetMapping("/errors")
    ResponseEntity<List<TraceSummaryDto>> getAllTracesWithError(
            @RequestParam(name = "serviceName", required = false) String serviceName,
            @RequestParam(name = "status", required = false) Long minDuration,
            @RequestParam(name = "sortBy", defaultValue = "DURATION") SortField sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "DESC") SortOrder sortOrder
    ) {
        var summaries = traceService.findAllTracesAsSummary(
                TraceFilterSpecification.createError(serviceName, minDuration),
                TraceSortSpecification.create(sortBy, sortOrder)
        );

        return ResponseEntity.ok(summaries.stream().map(this::mapToDto).toList());
    }

    @GetMapping
    ResponseEntity<List<TraceSummaryDto>> getAllTraces(
            @RequestParam(name = "serviceName", required = false) String serviceName,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "minDuration", required = false) Long minDuration,
            @RequestParam(name = "sortBy", defaultValue = "DURATION") SortField sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "DESC") SortOrder sortOrder
    ) {
        var filterSpec = TraceFilterSpecification.create(serviceName, status, minDuration);
        var sortSpec = TraceSortSpecification.create(sortBy, sortOrder);
        var summaries = traceService.findAllTracesAsSummary(filterSpec, sortSpec);

        return ResponseEntity.ok(summaries.stream().map(this::mapToDto).toList());
    }

    @GetMapping("{id}")
    ResponseEntity<TraceTreeDto> getWithId(@PathVariable("id") Optional<String> id) {
        String traceId = id.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'traceId' is required"));

        TraceTree traceTree = traceService.findTrace(traceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Trace with id: " + traceId + " not found.")
                );

        return ResponseEntity.ok(mapToDto(traceTree));
    }

    @GetMapping("{id}/tree")
    String getPrintTree(@PathVariable("id") Optional<String> id) {
        String traceId = id.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'traceId' is required"));

        TraceTree traceTree = traceService.findTrace(traceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Trace with id: " + traceId + " not found.")
                );

        return traceTree.printTree();
    }

    // ==== Mapping ====

    private TraceTreeDto mapToDto(TraceTree traceTree) {
        SpanDto rootDto = traceTree.getRoot() != null
                ? mapToDto(traceTree.getRoot())
                : null;

        return new TraceTreeDto(
                traceTree.getTraceId(),
                rootDto,
                traceTree.getOrphanSpans().stream().map(this::mapToDto).toList()
        );
    }

    private SpanDto mapToDto(Span span) {
        return new SpanDto(
                span.getServiceName(),
                span.getOperation(),
                span.getStatus(),
                span.getTimestamp(),
                span.getDuration(),
                span.getErrorMessage(),
                span.getType(),
                span.getChildren().stream().map(this::mapToDto).toList()
        );
    }

    private TraceSummaryDto mapToDto(TraceSummary summary) {
        return new TraceSummaryDto(
                summary.getTraceId(),
                summary.getTotalDuration(),
                summary.getServices(),
                summary.getSpanCount(),
                summary.getOverallStatus()
        );
    }
}

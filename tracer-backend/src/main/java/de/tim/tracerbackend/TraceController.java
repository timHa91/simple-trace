package de.tim.tracerbackend;

import de.tim.tracerbackend.dto.SpanDto;
import de.tim.tracerbackend.dto.TraceDto;
import de.tim.tracerbackend.dto.TraceTreeDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
}

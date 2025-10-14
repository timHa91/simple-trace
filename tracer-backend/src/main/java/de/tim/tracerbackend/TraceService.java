package de.tim.tracerbackend;

import de.tim.tracerbackend.dto.TraceDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TraceService {

    private final TraceCache cache;

    public TraceService(TraceCache cache) {
        this.cache = cache;
    }

    public void addTrace(TraceDto traceDto) {
        cache.put(traceDto);
    }

    public Optional<TraceTree> findTrace(String traceId) {
        TraceTree traceTree =  cache.get(traceId);

        if (traceTree == null) {
            return Optional.empty();
        }

        traceTree.buildTree();

        return Optional.of(traceTree);
    }

}

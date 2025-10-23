package de.tim.tracerbackend.repository;

import de.tim.tracerbackend.model.Span;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpanRepository extends JpaRepository<Span, String> {
    List<Span> findByTraceId(String traceId);
}

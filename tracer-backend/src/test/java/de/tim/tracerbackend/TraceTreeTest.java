package de.tim.tracerbackend;

import de.tim.tracerbackend.model.Span;
import de.tim.tracerbackend.model.TraceTree;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TraceTreeTest {

//
//    @Test
//    void shouldBuildTreeStructure_success() {
//        List<Span> spans = createSpans();
//
//        TraceTree traceTree = new TraceTree(
//                "da3155cd-527c-470a-9a95-f777ba8147c2"
//        );
//
//        for (var span : spans) {
//            traceTree.add(span);
//        }
//
//        traceTree.buildTree();
//
//        System.out.println(traceTree.printTree());
//
//    }
//
//    @Test
//    void shouldBuildTreeStructureWithOrphan_success() {
//        List<Span> spans = createSpans();
//        spans.add(createOrphanSpan());
//
//        TraceTree traceTree = new TraceTree(
//                "da3155cd-527c-470a-9a95-f777ba8147c2"
//        );
//
//        for (var span : spans) {
//            traceTree.add(span);
//        }
//
//        traceTree.buildTree();
//
//        System.out.println(traceTree.printTree());
//
//    }
//
//    private Span createOrphanSpan() {
//        return new Span(
//                    "orphan-1",
//                    "does-not-exist",
//                    "Service-D",
//                    "GET /test",
//                    200,
//                    Instant.now(),
//                    10L,
//                    null,
//                    "inbound"
//            );
//    }
//
//
//    private List<Span> createSpans() {
//        return new ArrayList<>(Arrays.asList(
//                new Span(
//                     "b8b6bf72-ef14-4d65-a545-aaece6d04d67",
//                        "f7c3bbe5-af8b-4362-9d5c-92bdc55a6153",
//                        "Service-C",
//                        "GET /api/work",
//                        200,
//                        Instant.now(),
//                        19L,
//                        null,
//                        "inbound"
//                ),
//                new Span(
//                       "f7c3bbe5-af8b-4362-9d5c-92bdc55a6153",
//                       "a15ac609-5f96-4262-9406-8fe081f6115f",
//                        "Service-B",
//                        "GET /api/work",
//                        200,
//                        Instant.now(),
//                        31L,
//                        null,
//                        "outbound"
//                ),
//                new Span(
//                        "1843461e-e0d6-4eda-897d-b5b73ef0380e",
//                        "f5240957-c7be-40e8-adc1-830d148e61e2",
//                        "Servcie-A",
//                        "GET /api/process",
//                        200,
//                        Instant.now(),
//                        209L,
//                        null,
//                        "outbound"
//                ),
//                new Span(
//                        "f5240957-c7be-40e8-adc1-830d148e61e2",
//                        null,
//                        "Service-A",
//                        "GET /api/start",
//                        200,
//                        Instant.now(),
//                        215L,
//                        null,
//                        "inbound"
//                ),
//                new Span(
//                        "a15ac609-5f96-4262-9406-8fe081f6115f",
//                        "1843461e-e0d6-4eda-897d-b5b73ef0380e",
//                        "Service-B",
//                        "GET /api/process",
//                        200,
//                        Instant.now(),
//                        181L,
//                        null,
//                        "inbound"
//                )
//        ));
//
//    }

}
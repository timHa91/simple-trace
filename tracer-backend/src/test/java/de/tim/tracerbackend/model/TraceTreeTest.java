package de.tim.tracerbackend.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TraceTreeTest {
    private static final String INDENT_1 = "  ";      // 2 Spaces
    private static final String INDENT_2 = "    ";    // 4 Spaces
    private static final String INDENT_3 = "      ";  // 6 Spaces
    private static final String INDENT_4 = "        "; // 8 Spaces

    @Test
    void shouldBuildTreeStructure_success() {
        List<Span> spans = createSpans();

        TraceTree traceTree = TraceTree.build(
                "da3155cd-527c-470a-9a95-f777ba8147c2",
                spans
        );

        String treeOutput = traceTree.printTree();

        assertThat(treeOutput).isNotNull();
        assertThat(treeOutput).containsPattern("Service-A \\[inbound\\] GET /api/start");
        assertThat(treeOutput).containsPattern("(?m)^" + indent(1) + "Service-A \\[outbound\\] GET /api/process");
        assertThat(treeOutput).containsPattern("(?m)^" + indent(2) + "Service-B \\[inbound\\] GET /api/process");
        assertThat(treeOutput).containsPattern("(?m)^" + indent(3) + "Service-B \\[outbound\\] GET /api/work");
        assertThat(treeOutput).containsPattern("(?m)^" + indent(4) + "Service-C \\[inbound\\] GET /api/work");
    }

    @Test
    void shouldBuildTreeStructureWithOrphan_success() {
        List<Span> spans = createSpans();
        spans.add(createOrphanSpan());

        TraceTree traceTree = TraceTree.build(
                "da3155cd-527c-470a-9a95-f777ba8147c2",
                spans
        );

        String treeOutput = traceTree.printTree();
        assertThat(treeOutput)
                .contains("Service-A [inbound] GET /api/start")
                .contains("Service-B [outbound] GET /api/work")
                .contains("Service-C [inbound] GET /api/work");

        assertThat(treeOutput)
                .containsPattern("(?m)^Orphaned Spans \\(Parent missing\\):")
                .containsPattern("(?m)^\\s*- Service-D \\[inbound\\] GET /test \\(10ms\\) \\(parent: does-not-exist\\)");

    }

    private String indent(int level) {
        return "  ".repeat(level); // 2 spaces per level
    }

    private Span createOrphanSpan() {
        return new Span(
                "orphan-1",
                    "trace-1",
                    "does-not-exist",
                    "Service-D",
                    "GET /test",
                    200,
                    Instant.now(),
                    10L,
                    null,
                    "inbound"
            );
    }


    private List<Span> createSpans() {
        return new ArrayList<>(Arrays.asList(
                new Span(
                        "b8b6bf72-ef14-4d65-a545-aaece6d04d67",
                        "trace-1",
                        "f7c3bbe5-af8b-4362-9d5c-92bdc55a6153",
                        "Service-C",
                        "GET /api/work",
                        200,
                        Instant.now(),
                        19L,
                        null,
                        "inbound"
                ),
                new Span(
                        "f7c3bbe5-af8b-4362-9d5c-92bdc55a6153",
                        "trace-1",
                        "a15ac609-5f96-4262-9406-8fe081f6115f",
                        "Service-B",
                        "GET /api/work",
                        200,
                        Instant.now(),
                        31L,
                        null,
                        "outbound"
                ),
                new Span(
                        "1843461e-e0d6-4eda-897d-b5b73ef0380e",
                        "trace-1",
                        "f5240957-c7be-40e8-adc1-830d148e61e2",
                        "Service-A",
                        "GET /api/process",
                        200,
                        Instant.now(),
                        209L,
                        null,
                        "outbound"
                ),
                new Span(
                        "f5240957-c7be-40e8-adc1-830d148e61e2",
                        "trace-1",
                        null,
                        "Service-A",
                        "GET /api/start",
                        200,
                        Instant.now(),
                        215L,
                        null,
                        "inbound"
                ),
                new Span(
                        "a15ac609-5f96-4262-9406-8fe081f6115f",
                        "trace-1",
                        "1843461e-e0d6-4eda-897d-b5b73ef0380e",
                        "Service-B",
                        "GET /api/process",
                        200,
                        Instant.now(),
                        181L,
                        null,
                        "inbound"
                )
        ));

    }

}
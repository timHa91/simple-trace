package de.tim.tracerbackend.model;

import de.tim.tracerbackend.SpanTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TraceTreeTest {

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

        assertThat(traceTree.getOrphanSpans())
                .hasSize(1)
                .first()
                .extracting(Span::getId, Span::getServiceName, Span::getParentId)
                .containsExactly("orphan-1", "Service-D", "does-not-exist");

    }

    private String indent(int level) {
        return "  ".repeat(level); // 2 spaces per level
    }

    private Span createOrphanSpan() {
        return new SpanTestBuilder()
                .id("orphan-1")
                .serviceName("Service-D")
                .parentId("does-not-exist")
                .build();
    }


    private List<Span> createSpans() {
        Span rootSpan = new SpanTestBuilder()
                .id("root-1")
                .parentId(null)
                .serviceName("Service-A")
                .operation("GET /api/start")
                .type("inbound")
                .build();

        Span serviceAOutbound = new SpanTestBuilder()
                .id("service-a-out")
                .parentId("root-1")
                .serviceName("Service-A")
                .operation("GET /api/process")
                .type("outbound")
                .build();

        Span serviceBInbound = new SpanTestBuilder()
                .id("service-b-in")
                .parentId("service-a-out")
                .serviceName("Service-B")
                .operation("GET /api/process")
                .type("inbound")
                .build();

        Span serviceBOutbound = new SpanTestBuilder()
                .id("service-b-out")
                .parentId("service-b-in")
                .serviceName("Service-B")
                .operation("GET /api/work")
                .type("outbound")
                .build();

        Span serviceCInbound = new SpanTestBuilder()
                .id("service-c-in")
                .parentId("service-b-out")
                .serviceName("Service-C")
                .operation("GET /api/work")
                .type("inbound")
                .build();


        return new ArrayList<>(
                Arrays.asList(rootSpan, serviceCInbound, serviceBInbound, serviceAOutbound, serviceBOutbound));
    }

}
package de.tim.tracerbackend.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TraceTree {

    private final String traceId;
    private Span root;
    private final List<Span> spans = new ArrayList<>();
    private final List<Span> orphanSpans = new ArrayList<>();

    public TraceTree(String traceId) {
        this.traceId = traceId;
    }

    public void add(Span span) {
        if (span == null || spans.contains(span)) {
            return;
        }
        spans.add(span);
    }

    public void buildTree() {
        if (spans.isEmpty()) {
            return;
        }

        Set<String> existingSpanIds = spans.stream()
                .map(Span::getId)
                .collect(Collectors.toSet());

        Map<String, List<Span>> parentToChildrenMapping = new HashMap<>();

        // 1. Creating Parent to Child Mapping
        for (var span : spans) {
            boolean isRootSpan = span.getParentId() == null;
            if (isRootSpan) {
                root = span;
            } else {
                boolean parentSpanDoesNotExist = !existingSpanIds.contains(span.getParentId());

                if (parentSpanDoesNotExist) {
                    this.orphanSpans.add(span);
                } else {
                    parentToChildrenMapping
                            .computeIfAbsent(span.getParentId(), v -> new ArrayList<>())
                            .add(span);
                }
            }
        }

        // 2. Setting children at existing Spans
        for (var span : spans) {
            span.setChildren(parentToChildrenMapping.getOrDefault(span.getId(), new ArrayList<>()));
        }
    }

    public String printTree() {
        if (root == null) return "";

        StringBuilder treeBuilder = new StringBuilder();
        AtomicInteger indent = new AtomicInteger(2);

        treeBuilder.append(root).append("\n");
        appendTree(treeBuilder, root.getChildren(), indent);

        if (!orphanSpans.isEmpty()) {
            treeBuilder.append("\nOrphaned Spans (Parent missing):\n");
            for (var orphan: orphanSpans) {
                treeBuilder.append("  - ").append(orphan)
                        .append(" (parent: ").append(orphan.getParentId()).append(")\n");
            }
        }

        return treeBuilder.toString();
    }

    private void appendTree(StringBuilder treeBuilder, List<Span> children, AtomicInteger indent) {
        for (var child : children) {
            treeBuilder.append(child.toString().indent(indent.get()));
            indent.getAndAdd(2);
            appendTree(treeBuilder, child.getChildren(), indent);
            indent.getAndAdd(-2);
        }
    }

    public String getTraceId() {
        return traceId;
    }

    public Span getRoot() {
        return root;
    }

    public List<Span> getSpans() {
        return spans;
    }

    public List<Span> getOrphanSpans() {
        return orphanSpans;
    }
}

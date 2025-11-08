import type { SpanDto, TraceSummaryDto, TraceTreeDto } from '@/types/api.ts'
import type { Span, Trace, TraceTree } from '@/types/traces.ts'

export class TraceMapper {

  static fromDtoSummary(dto: TraceSummaryDto): Trace {
    return {
      traceId: dto.traceId,
      spans: dto.spanCount,
      duration: dto.totalDuration,
      status: dto.overallStatus === 200 ? 'OK' : 'ERROR'
    }
  }

  static fromDtoTree(dto: TraceTreeDto): TraceTree {
    return {
      traceId: dto.traceId,
      rootSpan: this.fromDtoSpan(dto.rootSpan),
      orphans: dto.orphans?.map(TraceMapper.fromDtoSpan) ?? []
    }
  }

  static fromDtoSpan(dto: SpanDto): Span {
    return {
      serviceName: dto.serviceName,
      operation: dto.operation,
      status: dto.status,
      timestamp: dto.timestamp,
      duration: dto.duration,
      errorMessage: dto.errorMessage,
      type: dto.type,
      children: dto.children?.map(TraceMapper.fromDtoSpan) ?? []
    }
  }
}

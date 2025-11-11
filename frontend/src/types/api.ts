export interface TraceSummaryDto {
  traceId: string
  totalDuration: number
  services: string[]
  spanCount: number
  overallStatus: number
}

export interface TraceTreeDto {
      traceId: string
      rootSpan: SpanDto
      orphans?: SpanDto[]
}

export interface SpanDto {
      serviceName: string
      operation: string
      status: number
      timestamp: string
      duration: number
      errorMessage: string
      type: string
      children?: SpanDto[]
}

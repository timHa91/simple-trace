export enum TraceStatus {
  OK= 200 ,
  BAD_REQUEST = 400,
  ERROR = 500,
}

export type TraceSummaryDto = {
  traceId: string
  totalDuration: number
  services: string[]
  spanCount: number
  overallStatus: TraceStatus
}

export type TraceTreeDto = {
      traceId: string
      rootSpan: SpanDto
      orphans?: SpanDto[]
}

export type SpanDto = {
      serviceName: string
      operation: string
      status: number
      timestamp: string
      duration: number
      errorMessage: string
      type: string
      children?: SpanDto[]
}

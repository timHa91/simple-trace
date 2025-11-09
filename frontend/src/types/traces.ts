export type TraceStatus = "OK" | "ERROR";

export interface Trace {
  traceId: string
  spans: number
  duration: number
  status: TraceStatus
}

export interface TraceTree {
  traceId: string
  rootSpan: Span
  orphans: Span[]
}

export interface Span {
  serviceName: string
  operation: string
  status: number
  timestamp: string
  duration: number
  errorMessage: string
  type: string
  children?: Span[]
}


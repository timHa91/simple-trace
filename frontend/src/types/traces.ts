export type TraceStatus = "OK" | "ERROR";

export type Trace = {
  traceId: string;
  spans: number;
  duration: number;
  status: TraceStatus;
};

export type TraceTree = {
  traceId: string
  rootSpan: Span
  orphans: Span[]
}

export type Span = {
  serviceName: string
  operation: string
  status: number
  timestamp: string
  duration: number
  errorMessage: string
  type: string
  children?: Span[]
}


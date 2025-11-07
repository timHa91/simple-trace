export type TraceStatus = "OK" | "ERROR";

export type Trace = {
  traceId: string;
  spans: number;
  duration: number;
  status: TraceStatus;
};

export type SortOrder = "asc" | "desc"
export type SortBy = "span_count" | "duration" | "status"

export interface TraceSort {
  sortBy?: SortBy,
  sortOrder: SortOrder
}

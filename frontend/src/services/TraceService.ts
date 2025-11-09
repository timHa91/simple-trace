import type { Trace, TraceTree } from '@/types/traces.ts'
import axios from 'axios'
import { TraceMapper } from '@/mapper/TraceMapper.ts'
import type { TraceSummaryDto, TraceTreeDto } from '@/types/api.ts'
import type { TraceFilter } from '@/types/filter.ts'
import type { SortBy, SortOrder } from '@/types/sort.ts'

export class TraceService {
  private apiUrl = 'http://localhost:8080/api/traces'

  async getTrace(traceId: string): Promise<TraceTree> {
    try {
      const response = await axios.get<TraceTreeDto>(`${this.apiUrl}/${traceId}`)
      return TraceMapper.fromDtoTree(response.data)
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        throw new Error(`Trace ${traceId} not found`)
      }
      console.error(`Error fetching trace ${traceId}`, error)
      throw error
    }
  }

  async getAllFilteredTraces(filter: TraceFilter, sortBy: SortBy, sortOrder: SortOrder): Promise<Trace[]> {
    // Build Query-Params
    const queryParams: Record<string, string | number> = {}
    // Filter
    if (filter.serviceName && filter.serviceName.trim() !== '') queryParams.serviceName = filter.serviceName
    if (filter.minDuration) queryParams.minDuration = filter.minDuration
    if (filter.status) queryParams.status = filter.status
    // Sort
    queryParams.sortBy = sortBy
    queryParams.sortOrder = sortOrder

    try {
      const response = await axios.get<TraceSummaryDto[]>(this.apiUrl, { params: queryParams })
      return response.data.map((dto) => TraceMapper.fromDtoSummary(dto))
    } catch (error) {
      console.error('Error fetching traces', error)
      throw error
    }
  }
}

export const traceService = new TraceService()

import type { Trace, TraceTree } from '@/types/traces.ts'
import axios from 'axios'
import { TraceMapper } from '@/mapper/TraceMapper.ts'
import type { TraceSummaryDto, TraceTreeDto } from '@/types/api.ts'
import type { TraceFilter } from '@/types/filter.ts'
import type { TraceSort } from '@/types/sort.ts'

export class TraceService {
  private apiUrl: string

  constructor(apiUrl?: string) {
    this.apiUrl = apiUrl || import.meta.env.VITE_API_URL || 'http://localhost:8080/api/traces'
  }

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

  getErrorTraces(filter: TraceFilter, sort: TraceSort): Promise<Trace[]> {
    const errorUrl = this.apiUrl.concat('/errors')
    return this._getTraces(filter, sort, errorUrl)
  }

  getTraces(filter: TraceFilter, sort: TraceSort): Promise<Trace[]> {
    return this._getTraces(filter, sort, this.apiUrl)
  }

  private async _getTraces(filter: TraceFilter, sort: TraceSort, url: string): Promise<Trace[]> {
    const queryParams = Object.fromEntries(
      Object.entries(filter).filter(([_, value]) => value !== undefined)
    )
    Object.assign(queryParams, sort);

    try {
      const response = await axios.get<TraceSummaryDto[]>(
        url,
        {params: queryParams}
      )
      return response.data.map((dto) => TraceMapper.fromDtoSummary(dto))
    } catch (error) {
      console.error('Error fetching traces', error)
      throw error
    }
  }
}

export const traceService = new TraceService()

import type { Trace, TraceTree } from '@/types/traces.ts'
import axios from 'axios'
import { TraceMapper } from '@/mapper/TraceMapper.ts'
import type { TraceSummaryDto, TraceTreeDto } from '@/types/api.ts'

export class TraceService {
  private apiUrl = 'http://localhost:8080/api/traces'

  async getAllTraces(): Promise<Trace[]> {
    try {
      const response = await axios.get<TraceSummaryDto[]>(this.apiUrl)
      return response.data.map((dto) => TraceMapper.fromDtoSummary(dto))
    } catch (error) {
      console.error('Error fetching traces', error)
      throw error
    }
  }

  async getTrace(traceId: string): Promise<TraceTree> {
    try {
      const response =
        await axios.get<TraceTreeDto>(`${this.apiUrl}/${traceId}`)
      console.log(response)
      return TraceMapper.fromDtoTree(response.data);
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        throw new Error(`Trace ${traceId} not found`)
      }
      console.error(`Error fetching trace ${traceId}`, error)
      throw error
    }
  }

}

export const traceService = new TraceService()

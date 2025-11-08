<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { Trace } from '@/types/traces.ts'
import { traceService } from '@/services/TraceService.ts'

const traces = ref<Trace[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

async function loadTraces() {
  isLoading.value = true
  error.value = null

  try {
    traces.value = await traceService.getAllTraces()
  } catch (err) {
    error.value = 'Failed to load traces: ' + (err instanceof Error ? err.message : String(err))
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadTraces()
})

</script>

<template>
  <div v-if="isLoading">Loading traces...</div>
  <div v-else-if="error">{{ error }}</div>
  <div v-else>
    <table>
      <thead>
        <tr>
          <th>Trace ID</th>
          <th>Spans</th>
          <th>Duration</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="trace in traces" :key="trace.traceId">
          <td>
            <RouterLink :to="`/trace/${trace.traceId}`">
              {{ trace.traceId }}
            </RouterLink>
          </td>
          <td>{{ trace.spans }}</td>
          <td>{{ trace.duration }}</td>
          <td
            :class="{
              'status-ok': trace.status === 'OK',
              'status-error': trace.status === 'ERROR',
            }"
          >
            {{ trace.status }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.status-ok {
  background-color: #d4edda;
  color: #155724;
}

.status-error {
  background-color: #f8d7da;
  color: #721c24;
}
</style>

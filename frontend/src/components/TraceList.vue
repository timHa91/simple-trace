<script setup lang="ts">
import { ref } from 'vue'
import type { Trace } from '@/types/traces.ts'

const traces = ref<Trace[]>([
  { traceId: 'abc123', spans: 5, duration: 200, status: 'OK' },
  { traceId: 'def456', spans: 3, duration: 150, status: 'ERROR' },
  { traceId: 'ghi789', spans: 2, duration: 50, status: 'OK' },
])
</script>

<template>
  <div>
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
          <td>{{ trace.traceId }}</td>
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
  color: #155724
}

.status-error {
  background-color: #f8d7da;
  color: #721c24;
}

</style>

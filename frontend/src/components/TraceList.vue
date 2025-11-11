<script setup lang="ts">
import {computed, onMounted, ref} from 'vue'
import {Trace} from '@/types/traces.ts'
import {TraceFilter} from '@/types/filter.ts'
import {SortBy, TraceSort} from '@/types/sort.ts'

const traces = ref<Trace[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)
const filter = ref<TraceFilter>({})
const appliedFilter = ref<TraceFilter>({})
const sort = ref<TraceSort>({
  sortOrder: "desc",
  sortBy: "duration"
})

const props = defineProps<{
  fetchTraces: (filter: TraceFilter, sort: TraceSort) => Promise<Trace[]>
}>()

async function loadTraces() {
  if (isLoading.value) return

  isLoading.value = true
  error.value = null

  try {
    traces.value = await props.fetchTraces(
      filter.value,
      sort.value
    )
    appliedFilter.value = { ...filter.value }
  } catch (err) {
    error.value = 'Failed to load traces: ' + (err instanceof Error ? err.message : String(err))
  } finally {
    isLoading.value = false
  }
}

function resetFilter() {
  if (isFilterActive.value) {
    filter.value = {}
    loadTraces()
  }
}

const isFilterActive = computed(() => {
  return (
    appliedFilter.value.status !== undefined ||
    (appliedFilter.value.serviceName && appliedFilter.value.serviceName.trim() !== "") ||
    appliedFilter.value.minDuration !== undefined
  )
})

const isFilterDirty = computed( () => {
  return(
    filter.value.status !== appliedFilter.value.status ||
    filter.value.minDuration != appliedFilter.value.minDuration ||
    filter.value.serviceName != appliedFilter.value.serviceName
  )
})

function setSortField(value: SortBy) {
    if (sort.value.sortBy === value) {
      sort.value.sortOrder = sort.value.sortOrder === "desc" ? "asc" : "desc"
    } else {
      sort.value.sortBy = value
    }
    loadTraces()
}

onMounted(() => {
  loadTraces()
})
</script>

<template>
  <div v-if="isLoading" class="loading-spinner">Loading traces...</div>
  <div v-else-if="error" class="error-notification">{{ error }}</div>

  <div v-else>
    <div class="filter-bar">
      <!-- Service Name -->
      <input v-model="filter.serviceName" placeholder="Service Name" />

      <!-- Status Dropdown -->
      <select v-model.number="filter.status">
        <option :value="undefined">All</option>
        <option :value="200">OK (200)</option>
        <option :value="400">Bad Request (400)</option>
        <option :value="500">Error (500)</option>
      </select>

      <!-- Min Duration -->
      <input
        v-model.number="filter.minDuration"
        type="number"
        placeholder="Min Duration (ms)"
        min="0"
      />

      <!-- Filter Buttons -->
      <button @click="loadTraces" :disabled="!isFilterDirty || isLoading">Apply</button>
      <button @click="resetFilter" :disabled="!isFilterActive || isLoading">Reset</button>
    </div>
    <!-- Empty-State -->
    <div v-if="!isLoading && traces.length === 0">No traces found. Try adjusting your filters.</div>

    <!-- Trace-Table -->
    <table v-else class="trace-table">
      <thead>
        <tr>
          <th>Trace ID</th>
          <th @click="setSortField('span_count')" class="sortable">
            Spans
            <span v-if="sort.sortBy === 'span_count'">
              {{ sort.sortOrder === 'desc' ? '↓' : '↑' }}
            </span>
          </th>
          <th @click="setSortField('duration')" class="sortable">
            Duration
            <span v-if="sort.sortBy === 'duration'">
              {{ sort.sortOrder === 'desc' ? '↓' : '↑' }}
            </span>
          </th>
          <th @click="setSortField('status')" class="sortable">
            Status
            <span v-if="sort.sortBy === 'status'">
              {{ sort.sortOrder === 'desc' ? '↓' : '↑' }}
            </span>
          </th>
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

.sortable {
  cursor: pointer;
  user-select: none;
}

.sortable:hover {
  background-color: #f0f0f0;
}
</style>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { onBeforeRouteUpdate, useRoute } from 'vue-router'
import { traceService } from '@/services/TraceService.ts'
import type { TraceTree } from '@/types/traces'
import SpanNode from '@/components/SpanNode.vue'

const traceTree = ref<TraceTree | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)

async function loadTrace(id: string) {
  isLoading.value = true
  error.value = null
  try {
    traceTree.value = await traceService.getTrace(id)
  } catch (err) {
    error.value = 'Failed to load trace: ' + (err instanceof Error ? err.message : String(err))
  } finally {
    isLoading.value = false
  }
}

const route = useRoute()

onMounted(() => {
  const id = route.params.id as string
  loadTrace(id)
})

onBeforeRouteUpdate(async (to) => {
  const id = to.params.id as string
  if (id) await loadTrace(id)
})

</script>

<template>
  <div>
    <div v-if="isLoading">Loading trace…</div>
    <div v-else-if="error">{{ error }}</div>
    <div v-else-if="traceTree">
      <h3>{{ traceTree.traceId }}</h3>
      <SpanNode :span="traceTree.rootSpan"/>

      <div v-if="traceTree.orphans.length > 0">
        <h4>Orphan Spans:</h4>
        <SpanNode
          v-for="(orphan, index) in traceTree.orphans"
          :key="index"
          :span="orphan"
        />
      </div>
    </div>

  </div>
</template>

<style scoped></style>

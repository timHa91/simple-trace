<script setup lang="ts">
import type { Span } from '@/types/traces.ts'
import SpanNode from '@/components/SpanNode.vue'
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  span: Span
  depth?: number
}>(), {
  depth: 0
})

const indent = computed(() => {
  return { transform: `translateX(${props.depth * 30}px`}
})

</script>

<template>
  <div>
    <div :style="indent">
      <strong>{{ span.serviceName }}</strong> - {{ span.operation }}
      <span> {{ span.duration }}ms</span>
      <span v-if="span.errorMessage">{{ span.errorMessage }}</span>
    </div>
    <div v-if="span.children && span.children.length > 0">
      <SpanNode
        v-for="(child, index) in span.children"
        :key="index"
        :span="child"
        :depth="depth + 1"
      />
    </div>
  </div>
</template>

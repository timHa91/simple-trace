<script setup lang="ts">
import TraceList from "@/components/TraceList.vue";
import {traceService} from "@/services/TraceService.ts";
import type {TraceFilter} from "@/types/filter.ts";
import type {TraceSort} from "@/types/sort.ts";

const fetchErrorTraces = (filter: TraceFilter, sort: TraceSort) => traceService.getErrorTraces(filter, sort)
</script>

<template>
  <TraceList :fetchTraces="fetchErrorTraces" />
</template>

<style scoped></style>

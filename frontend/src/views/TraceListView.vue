<script setup lang="ts">
import TraceList from "@/components/TraceList.vue";
import {traceService} from "@/services/TraceService.ts";
import type {TraceFilter} from "@/types/filter.ts";
import type {TraceSort} from "@/types/sort.ts";

const fetchTraces = (filter: TraceFilter, sort: TraceSort) => traceService.getTraces(filter, sort)
</script>

<template>
  <TraceList :fetchTraces="fetchTraces"/>
</template>

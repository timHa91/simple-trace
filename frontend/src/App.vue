<script setup lang="ts">
import SideBar from "@/components/SideBar.vue";
</script>

<template>
  <div class="app-layout">
    <SideBar />
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
}
.main-content {
  flex: 1;
  padding: 1rem;
}
</style>

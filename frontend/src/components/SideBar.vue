<script setup lang="ts">

</script>
<template>
  <aside class="sidebar">
    <nav>
      <ul>
        <li><RouterLink to="/">Home</RouterLink></li>
        <li><RouterLink to="/traces">Traces</RouterLink></li>
        <li><RouterLink to="/errors">Errors</RouterLink></li>
      </ul>
    </nav>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 200px;
  background-color: #f8f9fa;
  padding: 1rem;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  margin-bottom: 0.5rem;
}
</style>

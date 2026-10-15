<template>
  <div class="layout">
    <HeaderBar />
    <main class="layout-main">
      <router-view />
    </main>
    <FooterBar />
  </div>
</template>

<script setup>
import HeaderBar from './HeaderBar.vue'
import FooterBar from './FooterBar.vue'
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.layout-main {
  flex: 1;
}
</style>
<template>
  <el-menu :default-active="$route.path" background-color="#1f2d3d" text-color="#bfcbd9"
           active-text-color="#ff8a5c" router class="sidebar-menu">
    <el-menu-item index="/dashboard">
      <el-icon><HomeFilled /></el-icon>
      <span>工作台</span>
    </el-menu-item>
    <template v-for="menu in menuTree" :key="menu.id">
      <el-sub-menu v-if="menu.children && menu.children.length" :index="'m-' + menu.id">
        <template #title>
          <el-icon><FolderOpened /></el-icon>
          <span>{{ menu.menuName }}</span>
        </template>
        <el-menu-item v-for="child in menu.children" :key="child.id" :index="'/' + child.path">
          <span>{{ child.menuName }}</span>
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item v-else :index="'/' + menu.path">
        <span>{{ menu.menuName }}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const menuTree = ref([])

onMounted(async () => {
  menuTree.value = await userStore.fetchMenus()
})
</script>

<style scoped>
.sidebar-menu {
  border-right: none;
}
</style>
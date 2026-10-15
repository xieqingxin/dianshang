<template>
  <header class="header">
    <div class="container header-inner">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-icon">🍍</span>
        <span class="logo-text">粤尝粤鲜</span>
      </div>

      <div class="search" @keyup.enter="goSearch">
        <el-input v-model="keyword" placeholder="搜索岭南鲜果" clearable>
          <template #append>
            <el-button @click="goSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <div class="actions">
        <el-badge :value="cartStore.count" :hidden="cartStore.count === 0" class="cart-badge">
          <el-button text @click="$router.push('/cart')">
            <el-icon :size="20"><ShoppingCart /></el-icon>
            <span>购物车</span>
          </el-button>
        </el-badge>

        <template v-if="userStore.isLogin">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              {{ userStore.userInfo?.nickname || '我的' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <el-button v-else type="primary" @click="$router.push('/login')">登录 / 注册</el-button>
      </div>
    </div>

    <div class="nav">
      <div class="container nav-inner">
        <span class="nav-item" :class="{ active: $route.path === '/' }" @click="$router.push('/')">首页</span>
        <span class="nav-item" :class="{ active: $route.path.startsWith('/products') }" @click="$router.push('/products')">全部鲜果</span>
        <span class="nav-item" v-for="c in categories" :key="c.id" @click="$router.push({ path: '/products', query: { categoryId: c.id } })">
          {{ c.icon }} {{ c.name }}
        </span>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { useCartStore } from '../stores/cart'
import { getCategories } from '../api'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const keyword = ref('')
const categories = ref([])

onMounted(async () => {
  try {
    categories.value = await getCategories()
  } catch (e) { /* ignore */ }
  if (userStore.isLogin) {
    cartStore.fetchCount()
  }
})

function goSearch() {
  router.push({ path: '/products', query: { keyword: keyword.value } })
}

function handleCommand(cmd) {
  if (cmd === 'orders') router.push('/orders')
  else if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  }
}
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.header-inner {
  display: flex;
  align-items: center;
  gap: 40px;
  padding-top: 16px;
  padding-bottom: 16px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.logo-icon {
  font-size: 28px;
}
.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary);
}
.search {
  flex: 1;
}
.actions {
  display: flex;
  align-items: center;
  gap: 20px;
}
.cart-badge {
  display: flex;
  align-items: center;
}
.user-dropdown {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: var(--text-main);
}
.nav {
  background: linear-gradient(90deg, #e65d36, #ff8a5c);
}
.nav-inner {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}
.nav-item {
  padding: 10px 16px;
  color: #fff;
  cursor: pointer;
  white-space: nowrap;
  font-size: 14px;
}
.nav-item:hover,
.nav-item.active {
  background: rgba(255, 255, 255, 0.25);
}
</style>
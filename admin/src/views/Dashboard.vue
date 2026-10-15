<template>
  <div>
    <h2 class="page-title">工作台</h2>
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background:#fff3ec;color:#e65d36">🍍</div>
        <div>
          <p class="stat-num">{{ stats.product }}</p>
          <p class="stat-label">商品总数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#ecf5ff;color:#409eff">📦</div>
        <div>
          <p class="stat-num">{{ stats.order }}</p>
          <p class="stat-label">订单总数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#f0f9eb;color:#67c23a">👤</div>
        <div>
          <p class="stat-num">{{ stats.user }}</p>
          <p class="stat-label">会员总数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#fef0f0;color:#f56c6c">🏷️</div>
        <div>
          <p class="stat-num">{{ stats.category }}</p>
          <p class="stat-label">分类总数</p>
        </div>
      </div>
    </div>

    <div class="welcome">
      <h3>欢迎使用粤尝粤鲜管理后台</h3>
      <p>左侧菜单可管理商品、分类、订单与会员用户。当前登录：{{ userStore.userInfo?.nickname || userStore.userInfo?.account }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getProductPage, getOrderPage, getUserPage, getCategoryList } from '../api'

const userStore = useUserStore()
const stats = ref({ product: 0, order: 0, user: 0, category: 0 })

onMounted(async () => {
  try {
    const p = await getProductPage({ page: 1, size: 1 })
    stats.value.product = Number(p.total)
  } catch (e) {}
  try {
    const o = await getOrderPage({ page: 1, size: 1 })
    stats.value.order = Number(o.total)
  } catch (e) {}
  try {
    const u = await getUserPage({ page: 1, size: 1 })
    stats.value.user = Number(u.total)
  } catch (e) {}
  try {
    const c = await getCategoryList()
    stats.value.category = c.length
  } catch (e) {}
})
</script>

<style scoped>
.page-title {
  font-size: 20px;
  margin-bottom: 20px;
}
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.stat-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.stat-num {
  font-size: 26px;
  font-weight: 700;
}
.stat-label {
  color: #909399;
  font-size: 13px;
}
.welcome {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  margin-top: 20px;
}
.welcome h3 {
  margin-bottom: 8px;
}
.welcome p {
  color: #909399;
}
@media (max-width: 900px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
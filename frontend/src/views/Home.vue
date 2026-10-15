<template>
  <div>
    <!-- 横幅 -->
    <div class="banner">
      <div class="container banner-content">
        <h1>粤尝粤鲜</h1>
        <p>岭南风味 · 树上熟鲜果 · 产地直发</p>
      </div>
    </div>

    <div class="container">
      <!-- 分类入口 -->
      <section class="section">
        <h2 class="section-title">鲜果分类</h2>
        <div class="category-grid">
          <div class="category-card" v-for="c in categories" :key="c.id"
               @click="$router.push({ path: '/products', query: { categoryId: c.id } })">
            <span class="category-icon">{{ c.icon }}</span>
            <span>{{ c.name }}</span>
          </div>
        </div>
      </section>

      <!-- 时令推荐 -->
      <section class="section">
        <h2 class="section-title">时令鲜果</h2>
        <div class="product-grid">
          <ProductCard v-for="p in seasonalProducts" :key="p.id" :product="p" />
        </div>
      </section>

      <!-- 热销 -->
      <section class="section">
        <h2 class="section-title">热销榜单</h2>
        <div class="product-grid">
          <ProductCard v-for="p in hotProducts" :key="p.id" :product="p" />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories, getProducts } from '../api'
import ProductCard from '../components/ProductCard.vue'

const categories = ref([])
const seasonalProducts = ref([])
const hotProducts = ref([])

onMounted(async () => {
  try {
    categories.value = await getCategories()
  } catch (e) { /* ignore */ }
  try {
    seasonalProducts.value = (await getProducts({ page: 1, size: 4 })).records.filter(p => p.isSeasonal === 1).slice(0, 4)
  } catch (e) { /* ignore */ }
  try {
    hotProducts.value = (await getProducts({ page: 1, size: 100 })).records.filter(p => p.isHot === 1).slice(0, 4)
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.banner {
  background: linear-gradient(120deg, #ff8a5c, #e65d36);
  color: #fff;
  padding: 48px 0;
  margin-bottom: 8px;
}
.banner-content h1 {
  font-size: 34px;
  margin-bottom: 8px;
}
.banner-content p {
  opacity: 0.9;
}
.section {
  margin-top: 32px;
}
.section-title {
  font-size: 20px;
  margin-bottom: 16px;
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}
.category-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 8px;
  transition: transform 0.2s, box-shadow 0.2s;
}
.category-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}
.category-icon {
  font-size: 32px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
@media (max-width: 900px) {
  .category-grid { grid-template-columns: repeat(3, 1fr); }
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
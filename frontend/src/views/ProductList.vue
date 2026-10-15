<template>
  <div class="container">
    <div class="filter-bar">
      <div class="filter-row">
        <span class="filter-label">分类</span>
        <span class="filter-item" :class="{ active: !currentCategoryId }" @click="selectCategory(null)">全部</span>
        <span class="filter-item" v-for="c in categories" :key="c.id"
              :class="{ active: currentCategoryId === c.id }" @click="selectCategory(c.id)">
          {{ c.icon }} {{ c.name }}
        </span>
      </div>
      <div class="filter-row">
        <span class="filter-label">产地</span>
        <span class="filter-item" :class="{ active: !origin }" @click="selectOrigin('')">全部</span>
        <span class="filter-item" v-for="o in origins" :key="o"
              :class="{ active: origin === o }" @click="selectOrigin(o)">{{ o }}</span>
      </div>
    </div>

    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="5" animated />
    </div>
    <template v-else>
      <div v-if="list.length" class="product-grid">
        <ProductCard v-for="p in list" :key="p.id" :product="p" />
      </div>
      <el-empty v-else description="暂无相关鲜果" />

      <div class="pagination">
        <el-pagination
          layout="prev, pager, next, total"
          :total="total"
          :page-size="size"
          :current-page="page"
          @current-change="onPageChange"
        />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCategories, getProducts } from '../api'
import ProductCard from '../components/ProductCard.vue'

const route = useRoute()
const categories = ref([])
const origins = ['广东', '海南']
const list = ref([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const size = 12
const currentCategoryId = ref(null)
const origin = ref('')
const keyword = ref('')

onMounted(async () => {
  getCategories().then(d => categories.value = d).catch(() => {})
  currentCategoryId.value = route.query.categoryId ? Number(route.query.categoryId) : null
  keyword.value = route.query.keyword || ''
  loadData()
})

function selectCategory(id) {
  currentCategoryId.value = id
  page.value = 1
  loadData()
}
function selectOrigin(o) {
  origin.value = o
  page.value = 1
  loadData()
}
function onPageChange(p) {
  page.value = p
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, size }
    if (currentCategoryId.value) params.categoryId = currentCategoryId.value
    if (origin.value) params.originPlace = origin.value
    if (keyword.value) params.keyword = keyword.value
    const data = await getProducts(params)
    list.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.filter-bar {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin: 20px 0;
}
.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}
.filter-row:last-child {
  margin-bottom: 0;
}
.filter-label {
  color: var(--text-secondary);
  font-size: 14px;
  min-width: 40px;
}
.filter-item {
  padding: 4px 14px;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
}
.filter-item:hover {
  color: var(--primary);
}
.filter-item.active {
  background: var(--primary);
  color: #fff;
}
.loading-wrap {
  background: #fff;
  padding: 24px;
  border-radius: 10px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin: 24px 0;
}
@media (max-width: 900px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
<template>
  <div class="container detail-wrap" v-if="product">
    <div class="detail-card">
      <div class="img-box">
        <img :src="imgSrc" :alt="product.name" @error="onImgError" />
      </div>
      <div class="info-box">
        <h1 class="name">{{ product.name }}</h1>
        <p class="origin">{{ product.originDetail || product.originPlace }} · {{ product.spec }}</p>
        <div class="price-row">
          <span class="price big">{{ product.price }}</span>
          <span class="sales">已售 {{ product.sales }} 件</span>
        </div>
        <p class="desc">{{ product.description }}</p>
        <div class="tags">
          <el-tag v-if="product.isSeasonal === 1" type="success">时令鲜果</el-tag>
          <el-tag v-if="product.isHot === 1" type="danger">热销</el-tag>
          <el-tag type="info">库存 {{ product.stock }}</el-tag>
        </div>
        <div class="buy-row">
          <el-input-number v-model="quantity" :min="1" :max="product.stock || 1" />
          <el-button type="primary" size="large" :disabled="product.stock <= 0" @click="addToCart">
            {{ product.stock > 0 ? '加入购物车' : '已售罄' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail, addCart } from '../api'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const quantity = ref(1)
const imgSrc = ref('')

onMounted(async () => {
  product.value = await getProductDetail(route.params.id)
  imgSrc.value = product.value.mainImage || ''
})

function onImgError() {
  const fruits = ['pineapple', 'lychee', 'mango', 'orange', 'blueberry', 'coconut']
  const idx = (product.value.id || 0) % fruits.length
  imgSrc.value = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent('fresh ' + fruits[idx] + ' fruit, clean product photo, white background')}&image_size=square_hd`
}

async function addToCart() {
  if (!localStorage.getItem('token')) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  await addCart({ productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}
</script>

<style scoped>
.detail-wrap {
  margin-top: 20px;
}
.detail-card {
  background: #fff;
  border-radius: 10px;
  display: flex;
  gap: 32px;
  padding: 32px;
}
.img-box {
  flex: 1;
  max-width: 420px;
  aspect-ratio: 1 / 1;
  border-radius: 10px;
  overflow: hidden;
  background: #f5f5f5;
}
.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.info-box {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.name {
  font-size: 26px;
  margin-bottom: 8px;
}
.origin {
  color: var(--text-secondary);
  margin-bottom: 16px;
}
.price-row {
  background: #fff7f3;
  padding: 16px;
  border-radius: 8px;
  display: flex;
  align-items: baseline;
  gap: 16px;
  margin-bottom: 16px;
}
.price.big {
  font-size: 32px;
}
.sales {
  color: var(--text-secondary);
  font-size: 13px;
}
.desc {
  color: #666;
  line-height: 1.8;
  margin-bottom: 16px;
}
.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}
.buy-row {
  display: flex;
  gap: 16px;
  margin-top: auto;
}
@media (max-width: 768px) {
  .detail-card { flex-direction: column; }
}
</style>
<template>
  <div class="product-card" @click="$router.push(`/product/${product.id}`)">
    <div class="img-wrap">
      <img :src="imgSrc" :alt="product.name" @error="onImgError" />
      <span v-if="product.isSeasonal === 1" class="tag seasonal">时令</span>
      <span v-if="product.isHot === 1" class="tag hot">热销</span>
    </div>
    <div class="info">
      <p class="name text-ellipsis">{{ product.name }}</p>
      <p class="origin">{{ product.originDetail || product.originPlace }}</p>
      <div class="bottom">
        <span class="price">{{ product.price }}</span>
        <span class="sales">已售{{ product.sales }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  product: { type: Object, required: true }
})

const imgSrc = ref(props.product.mainImage || '')

function onImgError() {
  imgSrc.value = fallbackImg(props.product.id)
}

const fruits = ['pineapple', 'lychee', 'mango', 'orange', 'blueberry', 'coconut']
function fallbackImg(id) {
  const idx = (id || 0) % fruits.length
  return `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent('fresh ' + fruits[idx] + ' fruit, clean product photo, white background, high quality')}&image_size=square_hd`
}
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}
.img-wrap {
  position: relative;
  aspect-ratio: 1 / 1;
  background: #f5f5f5;
}
.img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.tag {
  position: absolute;
  top: 8px;
  left: 8px;
  font-size: 12px;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
}
.tag.seasonal { background: #67c23a; }
.tag.hot { background: #e65d36; left: 52px; }
.info {
  padding: 12px;
}
.name {
  font-size: 15px;
  margin-bottom: 4px;
}
.origin {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  font-size: 18px;
}
.sales {
  font-size: 12px;
  color: var(--text-secondary);
}
</style>
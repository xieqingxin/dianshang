<template>
  <div class="container">
    <h2 class="page-title">购物车</h2>
    <div v-if="cart && cart.items && cart.items.length" class="cart-wrap">
      <div class="cart-list">
        <div class="cart-item" v-for="(item, idx) in cart.items" :key="item.productId">
          <img :src="item.productImage || fallback(idx)" class="item-img" alt="" />
          <div class="item-info">
            <p class="item-name">{{ item.productName }}</p>
            <p class="item-spec">{{ item.spec }} · {{ item.originDetail }}</p>
          </div>
          <div class="item-price price">{{ item.price }}</div>
          <div class="item-qty">
            <el-input-number v-model="item.quantity" :min="1" :max="item.stock || 1" size="small"
                             :disabled="item.status !== 1" @change="onChangeQty(item)" />
          </div>
          <div class="item-subtotal" v-if="item.status === 1">
            <span class="price">{{ item.totalPrice }}</span>
          </div>
          <div class="item-status" v-else>
            <el-tag type="danger" size="small">
              {{ item.status === 2 ? '库存不足' : '已失效' }}
            </el-tag>
          </div>
          <el-button link type="danger" @click="onDelete(item)">删除</el-button>
        </div>
      </div>

      <div class="cart-footer">
        <div class="summary">
          <span>共 {{ cart.totalCount }} 件</span>
          <span>合计：<span class="price big">{{ cart.totalAmount }}</span></span>
        </div>
        <el-button type="primary" size="large" :disabled="cart.totalCount === 0" @click="goCheckout">
          去结算
        </el-button>
      </div>
    </div>
    <el-empty v-else description="购物车空空如也">
      <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCart, updateCart, deleteCart } from '../api'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const cart = ref(null)

const fruits = ['pineapple', 'lychee', 'mango', 'orange', 'blueberry', 'coconut']
const fallback = (i) => `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent('fresh ' + fruits[i % fruits.length] + ' fruit, product photo, white background')}&image_size=square_hd`

onMounted(loadData)

async function loadData() {
  cart.value = await getCart()
  cartStore.count = cart.value.totalCount || 0
}

async function onChangeQty(item) {
  if (item.quantity > item.stock) {
    ElMessage.warning('库存不足')
    item.quantity = item.stock
    return
  }
  await updateCart({ productId: item.productId, quantity: item.quantity })
  loadData()
}

async function onDelete(item) {
  await deleteCart(item.productId)
  ElMessage.success('已删除')
  loadData()
}

function goCheckout() {
  router.push('/checkout')
}
</script>

<style scoped>
.cart-wrap {
  background: #fff;
  border-radius: 10px;
}
.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.item-img {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  object-fit: cover;
  background: #f5f5f5;
}
.item-info {
  flex: 1;
  min-width: 0;
}
.item-name {
  font-size: 15px;
  margin-bottom: 4px;
}
.item-spec {
  font-size: 12px;
  color: var(--text-secondary);
}
.item-price {
  width: 80px;
  text-align: center;
}
.item-qty {
  width: 130px;
}
.item-subtotal {
  width: 90px;
  text-align: center;
}
.item-status {
  width: 90px;
  text-align: center;
}
.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  padding: 16px;
}
.summary {
  display: flex;
  gap: 16px;
  align-items: baseline;
  color: var(--text-secondary);
}
.price.big {
  font-size: 24px;
}
</style>
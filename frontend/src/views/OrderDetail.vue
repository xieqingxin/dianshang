<template>
  <div class="container" v-if="order">
    <h2 class="page-title">订单详情</h2>
    <div class="status-banner">
      <el-tag :type="statusType(order.status)" size="large">{{ order.statusDesc }}</el-tag>
      <p class="status-tip">{{ statusTip(order.status) }}</p>
    </div>

    <div class="block">
      <h3>收货信息</h3>
      <p>{{ order.receiverName }} {{ order.receiverPhone }}</p>
      <p class="muted">{{ order.receiverAddress }}</p>
    </div>

    <div class="block">
      <h3>商品信息</h3>
      <div class="item" v-for="(it, i) in order.items" :key="i">
        <img :src="it.productImage || fallback(i)" alt="" />
        <div class="item-info">
          <p>{{ it.productName }}</p>
          <p class="muted">{{ it.spec }}</p>
        </div>
        <span class="muted">{{ it.price }} × {{ it.quantity }}</span>
        <span class="price">{{ it.totalPrice }}</span>
      </div>
      <div class="total-row">
        实付金额：<span class="price big">{{ order.totalAmount }}</span>
      </div>
    </div>

    <div class="block">
      <h3>订单信息</h3>
      <p class="muted">订单号：{{ order.orderNo }}</p>
      <p class="muted">创建时间：{{ order.createTime }}</p>
      <p class="muted" v-if="order.payTime">支付时间：{{ order.payTime }}</p>
      <p class="muted" v-if="order.shipTime">发货时间：{{ order.shipTime }}</p>
    </div>

    <div class="actions" v-if="order.status === 0">
      <el-button type="primary" @click="onPay">立即支付</el-button>
      <el-button @click="onCancel">取消订单</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, payOrder, cancelOrder } from '../api'

const route = useRoute()
const order = ref(null)
const fruits = ['pineapple', 'lychee', 'mango', 'orange', 'blueberry', 'coconut']
const fallback = (i) => `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent('fresh ' + fruits[i % fruits.length] + ' fruit, product photo, white background')}&image_size=square_hd`

function statusType(s) {
  return s === 0 ? 'warning' : s === 1 ? 'primary' : s === 2 ? 'success' : 'info'
}
function statusTip(s) {
  return { 0: '订单待支付，请尽快完成支付', 1: '订单已支付，等待发货', 2: '商品已发货，请注意查收', 3: '订单已取消' }[s] || ''
}

async function load() {
  order.value = await getOrderDetail(route.params.id)
}
async function onPay() {
  await payOrder(order.value.id)
  ElMessage.success('支付成功')
  load()
}
async function onCancel() {
  await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
  await cancelOrder(order.value.id)
  ElMessage.success('订单已取消')
  load()
}

onMounted(load)
</script>

<style scoped>
.status-banner {
  background: #fff;
  border-radius: 10px;
  padding: 20px 16px;
  margin-bottom: 16px;
}
.status-tip {
  margin-top: 12px;
  color: var(--text-secondary);
}
.block {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 16px;
}
.block h3 {
  font-size: 16px;
  margin-bottom: 12px;
}
.muted {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.8;
}
.item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}
.item img {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}
.item-info {
  flex: 1;
}
.total-row {
  text-align: right;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}
.price.big {
  font-size: 22px;
}
.actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>
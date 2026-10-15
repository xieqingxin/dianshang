<template>
  <div class="container">
    <h2 class="page-title">我的订单</h2>
    <el-tabs v-model="statusTab" @tab-change="onTabChange">
      <el-tab-pane label="全部" name="-1" />
      <el-tab-pane label="待支付" name="0" />
      <el-tab-pane label="已支付" name="1" />
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已取消" name="3" />
    </el-tabs>

    <div v-if="list.length">
      <div class="order-card" v-for="order in list" :key="order.id">
        <div class="order-head">
          <span>订单号：{{ order.orderNo }}</span>
          <span class="order-time">{{ order.createTime }}</span>
          <el-tag :type="statusType(order.status)" size="small">{{ order.statusDesc }}</el-tag>
        </div>
        <div class="order-body" @click="$router.push(`/order/${order.id}`)">
          <div class="order-imgs">
            <img v-for="(it, i) in order.items" :key="i" :src="it.productImage || fallback(i)" alt="" />
          </div>
          <div class="order-meta">
            <p>{{ order.items.map(i => i.productName).join('、') }}</p>
            <p class="order-amount">共 {{ totalQty(order) }} 件，实付 <span class="price">{{ order.totalAmount }}</span></p>
          </div>
        </div>
        <div class="order-actions">
          <el-button v-if="order.status === 0" type="primary" size="small" @click="onPay(order)">立即支付</el-button>
          <el-button v-if="order.status === 0" size="small" @click="onCancel(order)">取消订单</el-button>
          <el-button size="small" @click="$router.push(`/order/${order.id}`)">查看详情</el-button>
        </div>
      </div>
      <div class="pagination">
        <el-pagination layout="prev, pager, next, total" :total="total"
                       :page-size="size" :current-page="page" @current-change="onPageChange" />
      </div>
    </div>
    <el-empty v-else description="暂无订单" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, payOrder, cancelOrder } from '../api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 10
const statusTab = ref('-1')

const fruits = ['pineapple', 'lychee', 'mango', 'orange', 'blueberry', 'coconut']
const fallback = (i) => `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent('fresh ' + fruits[i % fruits.length] + ' fruit, product photo, white background')}&image_size=square_hd`

function statusType(s) {
  return s === 0 ? 'warning' : s === 1 ? 'primary' : s === 2 ? 'success' : 'info'
}
function totalQty(order) {
  return order.items.reduce((sum, i) => sum + i.quantity, 0)
}

function load() {
  const params = { page: page.value, size }
  if (statusTab.value !== '-1') params.status = Number(statusTab.value)
  getOrders(params).then(data => {
    list.value = data.records
    total.value = Number(data.total)
  })
}
function onTabChange() {
  page.value = 1
  load()
}
function onPageChange(p) {
  page.value = p
  load()
}

async function onPay(order) {
  await payOrder(order.id)
  ElMessage.success('支付成功')
  load()
}

async function onCancel(order) {
  await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
  await cancelOrder(order.id)
  ElMessage.success('订单已取消')
  load()
}

onMounted(load)
</script>

<style scoped>
.order-card {
  background: #fff;
  border-radius: 10px;
  margin-bottom: 16px;
}
.order-head {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
  color: var(--text-secondary);
}
.order-time {
  flex: 1;
}
.order-body {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  cursor: pointer;
}
.order-imgs {
  display: flex;
  gap: 8px;
}
.order-imgs img {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
}
.order-meta {
  flex: 1;
}
.order-amount {
  color: var(--text-secondary);
  font-size: 13px;
  margin-top: 8px;
}
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 0 16px 16px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin: 16px 0;
}
</style>
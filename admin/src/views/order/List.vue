<template>
  <div>
    <div class="toolbar">
      <el-input v-model="query.orderNo" placeholder="搜索订单号" clearable style="width:220px" @keyup.enter="load" />
      <el-select v-model="query.status" placeholder="订单状态" clearable style="width:150px" @change="load">
        <el-option label="待支付" :value="0" />
        <el-option label="已支付" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已取消" :value="3" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="receiverName" label="收货人" width="110">
        <template #default="{ row }">
          <div>{{ row.receiverName }}</div>
          <div class="muted">{{ row.receiverPhone }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="receiverAddress" label="收货地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column label="商品" min-width="160">
        <template #default="{ row }">{{ row.items.map(i => i.productName).join('、') }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ row.statusDesc }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="170" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="primary" @click="onShip(row)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination layout="total, prev, pager, next" :total="total" :page-size="query.size"
                     :current-page="query.page" @current-change="onPageChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, shipOrder } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, status: null, orderNo: '' })

function statusType(s) {
  return s === 0 ? 'warning' : s === 1 ? 'primary' : s === 2 ? 'success' : 'info'
}

async function load() {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (query.status !== null && query.status !== '') params.status = query.status
    if (query.orderNo) params.orderNo = query.orderNo
    const data = await getOrderPage(params)
    list.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}
function onPageChange(p) {
  query.page = p
  load()
}

async function onShip(row) {
  await ElMessageBox.confirm(`确认为订单 ${row.orderNo} 发货吗？`, '提示', { type: 'warning' })
  await shipOrder(row.id)
  ElMessage.success('发货成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.muted {
  color: #909399;
  font-size: 12px;
}
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
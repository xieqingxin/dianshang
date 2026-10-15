<template>
  <div class="container">
    <h2 class="page-title">确认订单</h2>
    <div class="checkout-grid">
      <div class="left">
        <div class="block">
          <div class="block-header">
            <h3>收货地址</h3>
            <el-button text type="primary" @click="addressDialog = true">+ 新增地址</el-button>
          </div>
          <el-radio-group v-model="addressId" class="address-list">
            <el-radio v-for="a in addresses" :key="a.id" :label="a.id" border class="address-item">
              <p class="addr-name">{{ a.receiverName }} <span class="addr-phone">{{ a.receiverPhone }}</span></p>
              <p class="addr-detail">{{ a.province }}{{ a.city }}{{ a.district }} {{ a.detailAddress }}</p>
              <el-tag v-if="a.isDefault === 1" size="small" type="warning">默认</el-tag>
            </el-radio>
          </el-radio-group>
        </div>

        <div class="block">
          <h3>商品清单</h3>
          <div class="checkout-item" v-for="(item, idx) in items" :key="item.productId">
            <img :src="item.productImage || fallback(idx)" alt="" />
            <div class="ci-info">
              <p>{{ item.productName }}</p>
              <p class="ci-spec">{{ item.spec }}</p>
            </div>
            <span>×{{ item.quantity }}</span>
            <span class="price">{{ item.totalPrice }}</span>
          </div>
        </div>
      </div>

      <div class="right">
        <div class="block">
          <h3>订单金额</h3>
          <div class="amount-row"><span>商品金额</span><span class="price">{{ totalAmount }}</span></div>
          <div class="amount-row"><span>运费</span><span>免运费</span></div>
          <div class="amount-row total"><span>应付总额</span><span class="price big">{{ totalAmount }}</span></div>
          <el-button type="primary" size="large" style="width:100%" :loading="submitting" @click="submitOrder">
            提交订单
          </el-button>
        </div>
      </div>
    </div>

    <!-- 新增地址弹窗 -->
    <el-dialog v-model="addressDialog" title="新增收货地址" width="480px">
      <el-form :model="addrForm" label-width="90px">
        <el-form-item label="收货人"><el-input v-model="addrForm.receiverName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="addrForm.receiverPhone" /></el-form-item>
        <el-form-item label="省市区">
          <div class="region">
            <el-input v-model="addrForm.province" placeholder="省" />
            <el-input v-model="addrForm.city" placeholder="市" />
            <el-input v-model="addrForm.district" placeholder="区" />
          </div>
        </el-form-item>
        <el-form-item label="详细地址"><el-input v-model="addrForm.detailAddress" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCart, getAddressList, addAddress, createOrder, clearCart } from '../api'

const router = useRouter()
const items = ref([])
const addresses = ref([])
const addressId = ref(null)
const addressDialog = ref(false)
const submitting = ref(false)
const addrForm = ref({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '' })

const fruits = ['pineapple', 'lychee', 'mango', 'orange', 'blueberry', 'coconut']
const fallback = (i) => `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent('fresh ' + fruits[i % fruits.length] + ' fruit, product photo, white background')}&image_size=square_hd`

const totalAmount = computed(() => {
  return items.value.filter(i => i.status === 1).reduce((sum, i) => sum + Number(i.totalPrice), 0).toFixed(2)
})

onMounted(async () => {
  const cart = await getCart()
  items.value = cart.items.filter(i => i.status === 1)
  addresses.value = await getAddressList()
  const def = addresses.value.find(a => a.isDefault === 1)
  addressId.value = def ? def.id : (addresses.value[0]?.id || null)
})

async function saveAddress() {
  await addAddress({ ...addrForm.value, isDefault: 0 })
  ElMessage.success('地址已保存')
  addressDialog.value = false
  addresses.value = await getAddressList()
}

async function submitOrder() {
  if (!addressId.value) return ElMessage.warning('请选择收货地址')
  if (!items.value.length) return ElMessage.warning('购物车为空')
  submitting.value = true
  try {
    const requestId = 'req-' + Date.now() + '-' + Math.random().toString(36).slice(2, 8)
    const orderId = await createOrder({
      addressId: addressId.value,
      requestId,
      items: items.value.map(i => ({ productId: i.productId, quantity: i.quantity }))
    })
    await clearCart()
    ElMessage.success('下单成功')
    router.push(`/order/${orderId}`)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.checkout-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 16px;
  align-items: start;
}
.block {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 16px;
}
.block-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.block h3 {
  font-size: 16px;
  margin-bottom: 12px;
}
.address-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}
.address-item {
  width: 100%;
  height: auto;
  padding: 12px;
}
.addr-name {
  font-weight: 600;
}
.addr-phone {
  color: var(--text-secondary);
  font-weight: 400;
  margin-left: 8px;
}
.addr-detail {
  font-size: 13px;
  color: #666;
  margin: 4px 0;
}
.checkout-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}
.checkout-item img {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}
.ci-info {
  flex: 1;
}
.ci-spec {
  font-size: 12px;
  color: var(--text-secondary);
}
.amount-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: var(--text-secondary);
}
.amount-row.total {
  color: var(--text-main);
  font-weight: 600;
  border-top: 1px solid #f0f0f0;
  margin: 8px 0 16px;
}
.price.big {
  font-size: 22px;
}
.region {
  display: flex;
  gap: 8px;
  width: 100%;
}
@media (max-width: 900px) {
  .checkout-grid { grid-template-columns: 1fr; }
}
</style>
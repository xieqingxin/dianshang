<template>
  <div class="container profile-wrap">
    <el-tabs>
      <el-tab-pane label="个人信息" lazy>
        <div class="block">
          <el-form :model="profileForm" label-width="90px" class="profile-form">
            <el-form-item label="手机号"><el-input :model-value="profile.phone" disabled /></el-form-item>
            <el-form-item label="昵称"><el-input v-model="profileForm.nickname" /></el-form-item>
            <el-form-item label="头像">
              <el-avatar :size="60" :src="profile.avatar">{{ profile.nickname?.charAt(0) }}</el-avatar>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <el-tab-pane label="收货地址" lazy>
        <div class="block">
          <div class="addr-header">
            <el-button type="primary" @click="openAddr(null)">+ 新增地址</el-button>
          </div>
          <el-empty v-if="!addresses.length" description="暂无收货地址" />
          <div class="addr-list">
            <div class="addr-item" v-for="a in addresses" :key="a.id">
              <div class="addr-content">
                <p class="addr-name">{{ a.receiverName }} <span class="addr-phone">{{ a.receiverPhone }}</span></p>
                <p class="addr-detail">{{ a.province }}{{ a.city }}{{ a.district }} {{ a.detailAddress }}</p>
                <el-tag v-if="a.isDefault === 1" size="small" type="warning">默认地址</el-tag>
              </div>
              <div class="addr-actions">
                <el-button v-if="a.isDefault !== 1" size="small" @click="setDefault(a)">设为默认</el-button>
                <el-button size="small" @click="openAddr(a)">编辑</el-button>
                <el-button size="small" type="danger" @click="delAddr(a)">删除</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="addrDialog" :title="editingId ? '编辑地址' : '新增地址'" width="480px">
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
        <el-form-item label="设为默认"><el-switch v-model="addrForm.isDefault" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addrDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddr">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProfile, updateProfile, getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '../api'

const profile = ref({})
const profileForm = reactive({ nickname: '', avatar: '' })
const addresses = ref([])
const addrDialog = ref(false)
const editingId = ref(null)
const addrForm = reactive({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '', isDefault: 0 })

onMounted(async () => {
  const p = await getProfile()
  profile.value = p
  profileForm.nickname = p.nickname
  profileForm.avatar = p.avatar
  loadAddr()
})

async function saveProfile() {
  await updateProfile(profileForm)
  ElMessage.success('保存成功')
}

async function loadAddr() {
  addresses.value = await getAddressList()
}

function openAddr(a) {
  if (a) {
    editingId.value = a.id
    Object.assign(addrForm, {
      receiverName: a.receiverName, receiverPhone: a.receiverPhone,
      province: a.province, city: a.city, district: a.district,
      detailAddress: a.detailAddress, isDefault: a.isDefault
    })
  } else {
    editingId.value = null
    Object.assign(addrForm, { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '', isDefault: 0 })
  }
  addrDialog.value = true
}

async function saveAddr() {
  const payload = { ...addrForm }
  if (editingId.value) {
    await updateAddress({ ...payload, id: editingId.value })
  } else {
    await addAddress(payload)
  }
  ElMessage.success('保存成功')
  addrDialog.value = false
  loadAddr()
}

async function setDefault(a) {
  await setDefaultAddress(a.id)
  loadAddr()
}

async function delAddr(a) {
  await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
  await deleteAddress(a.id)
  ElMessage.success('已删除')
  loadAddr()
}
</script>

<style scoped>
.profile-wrap {
  margin-top: 20px;
}
.block {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
}
.profile-form {
  max-width: 480px;
}
.addr-header {
  margin-bottom: 16px;
}
.addr-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  margin-bottom: 12px;
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
.addr-actions {
  display: flex;
  gap: 8px;
}
.region {
  display: flex;
  gap: 8px;
  width: 100%;
}
</style>
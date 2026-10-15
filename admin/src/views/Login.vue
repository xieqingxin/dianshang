<template>
  <div class="login-page">
    <div class="login-card">
      <div class="brand">
        <span class="brand-icon">🍍</span>
        <span class="brand-name">粤尝粤鲜 · 管理后台</span>
      </div>
      <el-form :model="form" @submit.prevent>
        <el-form-item>
          <el-input v-model="form.username" placeholder="账号" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="onLogin">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="onLogin">登录</el-button>
      </el-form>
      <p class="tip">默认账号：admin / 123456</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ username: '', password: '' })
const loading = ref(false)

async function onLogin() {
  if (!form.value.username || !form.value.password) return ElMessage.warning('请输入账号密码')
  loading.value = true
  try {
    await userStore.doLogin(form.value)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2d3d, #3a546d);
}
.login-card {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 36px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}
.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 24px;
}
.brand-icon {
  font-size: 30px;
}
.brand-name {
  font-size: 20px;
  font-weight: 700;
  color: #1f2d3d;
}
.tip {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin-top: 16px;
}
</style>
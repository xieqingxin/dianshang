<template>
  <div class="login-page">
    <div class="login-card">
      <div class="brand">
        <span class="brand-icon">🍍</span>
        <span class="brand-name">粤尝粤鲜</span>
      </div>
      <el-tabs v-model="mode" stretch>
        <el-tab-pane label="密码登录" name="login">
          <el-form :model="loginForm" label-width="0" @submit.prevent>
            <el-form-item>
              <el-input v-model="loginForm.phone" placeholder="手机号" size="large" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="loginForm.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="onLogin" />
            </el-form-item>
            <el-button type="primary" size="large" style="width:100%" @click="onLogin">登录</el-button>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="验证码登录" name="sms">
          <el-form :model="smsForm" label-width="0" @submit.prevent>
            <el-form-item>
              <el-input v-model="smsForm.phone" placeholder="手机号" size="large" />
            </el-form-item>
            <el-form-item>
              <div class="code-row">
                <el-input v-model="smsForm.code" placeholder="验证码" size="large" />
                <el-button :disabled="counting > 0" @click="sendCode">{{ counting > 0 ? counting + 's' : '获取验证码' }}</el-button>
              </div>
            </el-form-item>
            <el-button type="primary" size="large" style="width:100%" @click="onSmsLogin">登录</el-button>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="注册" name="register">
          <el-form :model="regForm" label-width="0" @submit.prevent>
            <el-form-item>
              <el-input v-model="regForm.phone" placeholder="手机号" size="large" />
            </el-form-item>
            <el-form-item>
              <div class="code-row">
                <el-input v-model="regForm.code" placeholder="验证码" size="large" />
                <el-button :disabled="counting > 0" @click="sendRegCode">{{ counting > 0 ? counting + 's' : '获取验证码' }}</el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.password" type="password" placeholder="设置密码" size="large" show-password />
            </el-form-item>
            <el-button type="primary" size="large" style="width:100%" @click="onRegister">注册并登录</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <p class="tip">测试账号：13800138000 / 123456</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { sendSmsCode, loginBySms, register } from '../api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const mode = ref('login')
const counting = ref(0)

const loginForm = ref({ phone: '', password: '' })
const smsForm = ref({ phone: '', code: '' })
const regForm = ref({ phone: '', code: '', password: '' })

function startCount() {
  counting.value = 60
  const t = setInterval(() => {
    counting.value--
    if (counting.value <= 0) clearInterval(t)
  }, 1000)
}

function redirect() {
  router.push(route.query.redirect || '/')
}

async function onLogin() {
  if (!loginForm.value.phone || !loginForm.value.password) return ElMessage.warning('请填写完整')
  await userStore.doLogin(loginForm.value)
  ElMessage.success('登录成功')
  redirect()
}

async function sendCode() {
  if (!smsForm.value.phone) return ElMessage.warning('请输入手机号')
  const code = await sendSmsCode(smsForm.value.phone)
  ElMessage.success('验证码已发送（开发环境）：' + code)
  startCount()
}

async function onSmsLogin() {
  const data = await loginBySms(smsForm.value)
  userStore.token = data.token
  userStore.userInfo = data
  localStorage.setItem('token', data.token)
  localStorage.setItem('userInfo', JSON.stringify(data))
  ElMessage.success('登录成功')
  redirect()
}

async function sendRegCode() {
  if (!regForm.value.phone) return ElMessage.warning('请输入手机号')
  const code = await sendSmsCode(regForm.value.phone)
  ElMessage.success('验证码已发送（开发环境）：' + code)
  startCount()
}

async function onRegister() {
  await register(regForm.value)
  const data = await loginBySms({ phone: regForm.value.phone, code: regForm.value.code })
  userStore.token = data.token
  userStore.userInfo = data
  localStorage.setItem('token', data.token)
  localStorage.setItem('userInfo', JSON.stringify(data))
  ElMessage.success('注册成功')
  redirect()
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(120deg, #ff9a6b, #e65d36);
}
.login-card {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}
.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
}
.brand-icon {
  font-size: 30px;
}
.brand-name {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary);
}
.code-row {
  display: flex;
  gap: 8px;
  width: 100%;
}
.tip {
  text-align: center;
  color: var(--text-secondary);
  font-size: 12px;
  margin-top: 12px;
}
</style>
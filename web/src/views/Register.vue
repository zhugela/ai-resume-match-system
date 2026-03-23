<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <div class="logo-icon">📋</div>
        <h1 class="register-title">创建账号</h1>
        <p class="register-subtitle">开启智能求职之旅</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large" class="register-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="邮箱（选填）"
            :prefix-icon="Message"
          />
        </el-form-item>

        <el-button
          type="primary"
          class="register-btn"
          :loading="loading"
          @click="handleRegister"
        >
          {{ loading ? '注册中...' : '立即注册' }}
        </el-button>
      </el-form>

      <div class="register-footer">
        <span class="footer-text">已有账号？</span>
        <router-link to="/login" class="footer-link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.register({
          username: form.username,
          password: form.password,
          email: form.email
        })
        if (success) {
          ElMessage.success('注册成功！')
          router.push('/')
        }
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-primary);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: 40px;
  box-shadow: var(--shadow-lg);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  font-size: 40px;
  margin-bottom: 16px;
}

.register-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.register-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
}

.register-form {
  margin-bottom: 24px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.register-btn {
  width: 100%;
  height: 44px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  border-radius: var(--radius-sm) !important;
  background: var(--accent-primary) !important;
  border-color: var(--accent-primary) !important;
}

.register-btn:hover {
  background: var(--accent-secondary) !important;
  border-color: var(--accent-secondary) !important;
}

.register-footer {
  text-align: center;
  font-size: 14px;
}

.footer-text {
  color: var(--text-secondary);
}

.footer-link {
  color: var(--accent-primary);
  margin-left: 6px;
  font-weight: 500;
}

.footer-link:hover {
  color: var(--accent-secondary);
}
</style>

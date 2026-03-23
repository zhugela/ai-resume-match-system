<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">📋</div>
        <h1 class="login-title">欢迎回来</h1>
        <p class="login-subtitle">登录你的账号继续使用</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large" class="login-form">
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
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-button
          type="primary"
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
      </el-form>

      <div class="login-footer">
        <span class="footer-text">还没有账号？</span>
        <router-link to="/register" class="footer-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.login(form)
        if (success) {
          ElMessage.success('登录成功')
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
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-primary);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  padding: 40px;
  box-shadow: var(--shadow-lg);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  font-size: 40px;
  margin-bottom: 16px;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 44px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  border-radius: var(--radius-sm) !important;
  background: var(--accent-primary) !important;
  border-color: var(--accent-primary) !important;
}

.login-btn:hover {
  background: var(--accent-secondary) !important;
  border-color: var(--accent-secondary) !important;
}

.login-footer {
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

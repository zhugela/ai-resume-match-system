<template>
  <div class="layout-container">
    <!-- 极简顶部导航 -->
    <header class="top-header">
      <div class="header-left">
        <div class="logo">
          <span class="logo-icon">📋</span>
          <span class="logo-text">简历助手</span>
        </div>
      </div>

      <nav class="header-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-link"
          :class="{ active: isActive(item.path) }"
        >
          {{ item.title }}
        </router-link>
      </nav>

      <div class="header-right">
        <el-dropdown @command="handleCommand" trigger="click">
          <div class="user-btn">
            <span class="user-avatar">{{ (userStore.userInfo.username || 'U')[0].toUpperCase() }}</span>
            <span class="user-name">{{ userStore.userInfo.username || '用户' }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <span>个人中心</span>
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const menuItems = [
  { path: '/home', title: '首页' },
  { path: '/resume', title: '简历' },
  { path: '/job', title: '岗位' },
  { path: '/match', title: '匹配' },
  { path: '/questions', title: '题库' },
  { path: '/salary', title: '薪资' },
  { path: '/templates', title: '模板' },
  { path: '/progress', title: '进度' },
  { path: '/compare-resumes', title: '对比' },
  { path: '/notifications', title: '消息' }
]

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    })
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
/* 布局容器 */
.layout-container {
  min-height: 100vh;
  background: var(--bg-primary);
}

/* 顶部导航 */
.top-header {
  height: 60px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 100;
}

/* Logo */
.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 20px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 导航 */
.header-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.15s ease;
}

.nav-link:hover {
  color: var(--text-primary);
  background: var(--bg-tertiary);
}

.nav-link.active {
  color: var(--accent-primary);
  background: rgba(217, 119, 6, 0.08);
}

.nav-icon {
  font-size: 15px;
}

/* 用户 */
.header-right {
  display: flex;
  align-items: center;
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
}

.user-btn:hover {
  background: var(--bg-tertiary);
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: var(--bg-tertiary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

/* 主内容 */
.main-content {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* 响应式 */
@media (max-width: 900px) {
  .nav-text { display: none; }
  .nav-link { padding: 8px 10px; }
}
@media (max-width: 600px) {
  .header-nav { display: none; }
}
</style>

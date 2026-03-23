<template>
  <div class="home">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-left">
        <h1 class="welcome-title">
          你好，<span class="name">{{ userStore.userInfo.username || '朋友' }}</span>
        </h1>
        <p class="welcome-desc">
          有什么可以帮助你的吗？
        </p>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" @click="$router.push('/resume')">
          上传简历
        </el-button>
        <el-button @click="$router.push('/job')">
          浏览岗位
        </el-button>
      </div>
    </div>

    <!-- 数据卡片 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="(stat, index) in statsData" :key="index">
        <div class="stat-icon" :style="{ background: stat.color }">
          <el-icon size="20"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="section">
      <h2 class="section-title">快捷操作</h2>
      <div class="actions-grid">
        <div
          class="action-card"
          v-for="action in actions"
          :key="action.title"
          @click="$router.push(action.path)"
        >
          <div class="action-icon" :style="{ background: action.color }">
            <el-icon size="20"><component :is="action.icon" /></el-icon>
          </div>
          <div class="action-info">
            <div class="action-title">{{ action.title }}</div>
            <div class="action-desc">{{ action.desc }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 使用指南 -->
    <div class="section">
      <h2 class="section-title">使用流程</h2>
      <div class="steps-card">
        <div class="step-item" v-for="(step, index) in steps" :key="index">
          <div class="step-num">{{ index + 1 }}</div>
          <div class="step-info">
            <div class="step-title">{{ step.title }}</div>
            <div class="step-desc">{{ step.desc }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { jobApi } from '@/api'
import {
  Upload, Search, Document, Connection,
  Briefcase, Setting
} from '@element-plus/icons-vue'

const userStore = useUserStore()

const statsData = ref([
  {
    icon: Document,
    value: 0,
    label: '简历',
    color: '#FEF3C7'
  },
  {
    icon: Briefcase,
    value: 0,
    label: '岗位',
    color: '#D1FAE5'
  },
  {
    icon: Connection,
    value: 0,
    label: '匹配',
    color: '#E0E7FF'
  },
  {
    icon: Setting,
    value: 0,
    label: '设置',
    color: '#FCE7F3'
  }
])

const actions = [
  {
    icon: Upload,
    title: '上传简历',
    desc: '上传 PDF 或 Word 简历',
    path: '/resume',
    color: '#FEF3C7'
  },
  {
    icon: Search,
    title: '浏览岗位',
    desc: '查看最新招聘信息',
    path: '/job',
    color: '#D1FAE5'
  },
  {
    icon: Connection,
    title: '智能匹配',
    desc: 'AI 计算匹配度',
    path: '/match',
    color: '#E0E7FF'
  },
  {
    icon: Setting,
    title: '个人设置',
    desc: '管理个人信息',
    path: '/profile',
    color: '#FCE7F3'
  }
]

const steps = [
  { title: '上传简历', desc: '上传 PDF 或 Word 简历' },
  { title: 'AI 分析', desc: '智能提取技能和经历' },
  { title: '浏览岗位', desc: '筛选心仪职位' },
  { title: '智能匹配', desc: '获取匹配度和建议' }
]

onMounted(async () => {
  try {
    const res = await jobApi.list({ pageNum: 1, pageSize: 1 })
    if (res.data.data) {
      statsData.value[1].value = res.data.data.total || 0
    }
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
/* 欢迎区域 */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px 0;
  margin-bottom: 32px;
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.name {
  color: var(--accent-primary);
}

.welcome-desc {
  font-size: 15px;
  color: var(--text-secondary);
}

.welcome-actions {
  display: flex;
  gap: 12px;
}

.welcome-actions .el-button {
  padding: 10px 20px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 40px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 2px;
}

/* 区块 */
.section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

/* 操作卡片 */
.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}

.action-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.action-desc {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 步骤卡片 */
.steps-card {
  background: var(--bg-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.step-num {
  width: 28px;
  height: 28px;
  background: var(--bg-tertiary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.step-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.step-desc {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .steps-card {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .actions-grid {
    grid-template-columns: 1fr;
  }

  .steps-card {
    grid-template-columns: 1fr;
  }
}
</style>

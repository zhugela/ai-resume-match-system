<template>
  <div class="admin-container">
    <h2>管理后台</h2>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon blue"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon green"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.resumeCount }}</div>
            <div class="stat-label">简历数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon orange"><el-icon><Briefcase /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.jobCount }}</div>
            <div class="stat-label">岗位数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon purple"><el-icon><DataLine /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.matchCount }}</div>
            <div class="stat-label">匹配次数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户管理" name="users">
        <el-table :data="users" border stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="phone" label="电话" />
          <el-table-column prop="role" label="角色">
            <template #default="{ row }">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'">
                {{ row.role || 'USER' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="注册时间" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="deleteUser(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="反馈管理" name="feedback">
        <el-table :data="feedbacks" border stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="userId" label="用户ID" width="100" />
          <el-table-column prop="content" label="反馈内容" />
          <el-table-column prop="type" label="类型" width="120">
            <template #default="{ row }">
              <el-tag>{{ row.type || '建议' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="row.status === 'RESOLVED' ? 'success' : 'warning'">
                {{ row.status === 'RESOLVED' ? '已处理' : '待处理' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="180" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="success" size="small" @click="resolveFeedback(row)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="热门岗位" name="hotjobs">
        <el-table :data="hotJobs" border stripe>
          <el-table-column prop="title" label="岗位名称" />
          <el-table-column prop="company" label="公司" />
          <el-table-column prop="matchCount" label="匹配次数" width="120">
            <template #default="{ row }">
              <el-badge :value="row.matchCount" class="badge" />
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Document, Briefcase, DataLine } from '@element-plus/icons-vue'
import api from '../api'

const activeTab = ref('users')
const stats = ref({
  userCount: 0,
  resumeCount: 0,
  jobCount: 0,
  matchCount: 0
})
const users = ref([])
const feedbacks = ref([])
const hotJobs = ref([])

const loadStats = async () => {
  try {
    const res = await api.get('/api/statistics/overview')
    stats.value = res.data || stats.value
  } catch (err) {
    console.error(err)
  }
}

const loadUsers = async () => {
  try {
    const res = await api.get('/api/admin/users')
    users.value = res.data || []
  } catch (err) {
    console.error(err)
  }
}

const loadFeedbacks = async () => {
  try {
    const res = await api.get('/api/feedback/list')
    feedbacks.value = res.data || []
  } catch (err) {
    console.error(err)
  }
}

const loadHotJobs = async () => {
  try {
    const res = await api.get('/api/statistics/hot-jobs')
    hotJobs.value = res.data || []
  } catch (err) {
    console.error(err)
  }
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/api/admin/user/${user.id}`)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const resolveFeedback = async (feedback) => {
  try {
    await api.put(`/api/feedback/${feedback.id}/resolve`)
    ElMessage.success('处理成功')
    loadFeedbacks()
  } catch (err) {
    ElMessage.error('处理失败')
  }
}

onMounted(() => {
  loadStats()
  loadUsers()
  loadFeedbacks()
  loadHotJobs()
})
</script>

<style scoped>
.admin-container {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  margin-right: 15px;
}

.stat-icon.blue { background: #409EFF; }
.stat-icon.green { background: #67C23A; }
.stat-icon.orange { background: #E6A23C; }
.stat-icon.purple { background: #909399; }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.badge {
  margin-top: 5px;
}
</style>

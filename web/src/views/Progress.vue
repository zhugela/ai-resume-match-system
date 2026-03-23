<template>
  <div class="progress-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">求职进度</h1>
        <p class="page-subtitle">记录你的求职历程，跟踪每一个面试机会</p>
      </div>
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
        添加投递
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row mb-20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">投递总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value" style="color: #409eff">{{ stats.interviewing }}</div>
            <div class="stat-label">面试中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value" style="color: #67c23a">{{ stats.offers }}</div>
            <div class="stat-label">已拿Offer</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value" style="color: #f56c6c">{{ stats.rejected }}</div>
            <div class="stat-label">已拒绝</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 进度列表 -->
    <el-card>
      <el-table :data="applications" v-loading="loading">
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="position" label="职位" width="180" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="salary" label="薪资" width="120" />
        <el-table-column prop="interviewTime" label="面试时间" width="160">
          <template #default="{ row }">
            {{ row.interviewTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="updateStatus(row)">更新状态</el-button>
            <el-button type="success" link @click="editApp(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteApp(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="showAddDialog" :title="editingApp?.id ? '编辑投递' : '添加投递'" width="500px">
      <el-form :model="appForm" label-width="80px">
        <el-form-item label="公司">
          <el-input v-model="appForm.company" placeholder="公司名称" />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="appForm.position" placeholder="职位名称" />
        </el-form-item>
        <el-form-item label="薪资">
          <el-input v-model="appForm.salary" placeholder="如：20K-30K" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="appForm.status" style="width: 100%">
            <el-option label="已投递" value="APPLIED" />
            <el-option label="简历筛选中" value="REVIEWING" />
            <el-option label="面试中" value="INTERVIEW" />
            <el-option label="已拿到Offer" value="OFFER" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已撤回" value="WITHDRAWN" />
          </el-select>
        </el-form-item>
        <el-form-item label="面试时间">
          <el-date-picker v-model="appForm.interviewTime" type="datetime" placeholder="选择时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="appForm.notes" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveApp">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { applicationApi } from '../api'

const loading = ref(false)
const applications = ref([])
const showAddDialog = ref(false)
const editingApp = ref(null)

const appForm = reactive({
  company: '',
  position: '',
  salary: '',
  status: 'APPLIED',
  interviewTime: '',
  notes: ''
})

const stats = computed(() => ({
  total: applications.value.length,
  interviewing: applications.value.filter(a => a.status === 'INTERVIEW').length,
  offers: applications.value.filter(a => a.status === 'OFFER').length,
  rejected: applications.value.filter(a => a.status === 'REJECTED').length
}))

const getStatusType = (status) => {
  const map = { APPLIED: 'info', REVIEWING: 'warning', INTERVIEW: 'primary', OFFER: 'success', REJECTED: 'danger', WITHDRAWN: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { APPLIED: '已投递', REVIEWING: '简历筛选中', INTERVIEW: '面试中', OFFER: '已拿Offer', REJECTED: '已拒绝', WITHDRAWN: '已撤回' }
  return map[status] || status
}

const loadApplications = async () => {
  loading.value = true
  try {
    const res = await applicationApi.list({ pageNum: 1, pageSize: 100 })
    applications.value = res.data.data || []
  } catch (e) {
    // 演示数据
    applications.value = [
      { id: 1, company: '字节跳动', position: 'Python后端', status: 'INTERVIEW', salary: '25K-40K', interviewTime: '2024-02-15 14:00', notes: '二面' },
      { id: 2, company: '阿里巴巴', position: 'Java开发', status: 'REVIEWING', salary: '20K-35K', notes: '等消息' },
      { id: 3, company: '腾讯', position: 'Go开发', status: 'OFFER', salary: '30K-45K', notes: '已接收' },
    ]
  }
  loading.value = false
}

const saveApp = async () => {
  try {
    if (editingApp.value?.id) {
      await applicationApi.update(editingApp.value.id, appForm)
      ElMessage.success('更新成功')
    } else {
      await applicationApi.add(appForm)
      ElMessage.success('添加成功')
    }
    showAddDialog.value = false
    loadApplications()
    resetForm()
  } catch (e) {
    // 演示模式
    if (!editingApp.value?.id) {
      applications.value.push({ ...appForm, id: Date.now() })
      ElMessage.success('添加成功（演示）')
    }
    showAddDialog.value = false
    resetForm()
  }
}

const editApp = (row) => {
  editingApp.value = row
  Object.assign(appForm, row)
  showAddDialog.value = true
}

const updateStatus = async (row) => {
  const statusOptions = ['APPLIED', 'REVIEWING', 'INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN']
  const statusTexts = ['已投递', '简历筛选中', '面试中', '已拿Offer', '已拒绝', '已撤回']
  const currentIndex = statusOptions.indexOf(row.status)
  const nextStatus = statusOptions[(currentIndex + 1) % statusOptions.length]
  
  try {
    await ElMessageBox.confirm(`确认更新状态为"${statusTexts[statusOptions.indexOf(nextStatus)]}"？`, '提示')
    row.status = nextStatus
    ElMessage.success('更新成功')
  } catch {}
}

const deleteApp = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除这条记录吗？', '提示', { type: 'warning' })
    applications.value = applications.value.filter(a => a.id !== row.id)
    ElMessage.success('删除成功')
  } catch {}
}

const resetForm = () => {
  editingApp.value = null
  Object.assign(appForm, { company: '', position: '', salary: '', status: 'APPLIED', interviewTime: '', notes: '' })
}

onMounted(loadApplications)
</script>

<style scoped>
.progress-page { padding: 32px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { margin: 0; font-size: 28px; font-weight: 700; }
.page-subtitle { color: #999; margin: 8px 0 0; }
.mb-20 { margin-bottom: 20px; }
.stats-row .stat-item { text-align: center; padding: 20px 0; }
.stats-row .stat-value { font-size: 36px; font-weight: bold; }
.stats-row .stat-label { color: #999; margin-top: 8px; }
</style>

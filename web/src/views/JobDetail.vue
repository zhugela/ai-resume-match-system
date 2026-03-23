<template>
  <div class="detail-page">
    <el-page-header @back="$router.back()" title="返回">
      <template #content>
        <span class="page-title">岗位详情</span>
      </template>
    </el-page-header>
    
    <el-card class="mt-20" v-if="job">
      <div class="job-header">
        <h2>{{ job.title }}</h2>
        <div class="job-meta">
          <el-tag type="success">{{ job.status === 'OPEN' ? '招聘中' : '已关闭' }}</el-tag>
          <span><el-icon><Location /></el-icon> {{ job.location }}</span>
          <span><el-icon><Money /></el-icon> {{ job.salaryRange }}</span>
        </div>
      </div>

      <el-descriptions :column="2" border class="mt-20">
        <el-descriptions-item label="公司">{{ job.company }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ job.jobType }}</el-descriptions-item>
        <el-descriptions-item label="浏览量">{{ job.views }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatDate(job.createdAt) }}</el-descriptions-item>
      </el-descriptions>

      <div class="job-section">
        <h4>岗位描述</h4>
        <p>{{ job.description || '暂无描述' }}</p>
      </div>

      <div class="job-section">
        <h4>岗位要求</h4>
        <p>{{ job.requirements || '暂无要求' }}</p>
      </div>

      <div class="job-section">
        <h4>必备技能</h4>
        <div class="skill-tags">
          <el-tag v-for="skill in parseSkills(job.requiredSkills)" :key="skill" type="danger">
            {{ skill }}
          </el-tag>
        </div>
      </div>

      <div class="job-actions mt-20">
        <el-button type="primary" @click="goToMatch">立即匹配</el-button>
      </div>
    </el-card>
    <el-skeleton v-else :rows="5" animated />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { jobApi } from '@/api'
import { Location, Money } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const job = ref(null)

onMounted(async () => {
  try {
    const res = await jobApi.getById(route.params.id)
    job.value = res.data.data
  } catch (e) {
    console.error(e)
  }
})

const parseSkills = (skills) => {
  try {
    return skills ? JSON.parse(skills) : []
  } catch {
    return []
  }
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : '-'
}

const goToMatch = () => {
  router.push({ path: '/match', query: { jobId: job.value.id } })
}
</script>

<style scoped>
.page-title { font-weight: 600; }
.mt-20 { margin-top: 20px; }
.job-header h2 { margin-bottom: 10px; }
.job-meta { display: flex; gap: 15px; align-items: center; color: #606266; }
.job-meta span { display: flex; align-items: center; gap: 5px; }
.job-section { margin-top: 20px; }
.job-section h4 { margin-bottom: 10px; color: #303133; }
.job-section p { line-height: 1.8; color: #606266; }
.skill-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.job-actions { text-align: center; }
</style>

<template>
  <div class="job-page">
    <!-- 搜索栏 -->
    <el-card class="mb-20">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索岗位" clearable />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="searchForm.location" placeholder="工作地点" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 岗位列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>岗位列表</span>
          <el-button type="primary" size="small" @click="showPublishDialog = true">
            发布岗位
          </el-button>
        </div>
      </template>
      
      <el-table :data="jobList" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="岗位名称" min-width="180">
          <template #default="{ row }">
            <el-link type="primary" @click="viewJob(row)">{{ row.title }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="location" label="地点" width="120" />
        <el-table-column prop="salaryRange" label="薪资" width="120" />
        <el-table-column prop="jobType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getJobTypeText(row.jobType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="views" label="浏览" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'OPEN' ? 'success' : 'info'">
              {{ row.status === 'OPEN' ? '开放' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewJob(row)">查看</el-button>
            <el-button type="success" link @click="matchJob(row)">匹配</el-button>
            <el-button type="warning" link @click="editJob(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadJobList"
          @current-change="loadJobList"
        />
      </div>
    </el-card>

    <!-- 发布/编辑对话框 -->
    <el-dialog v-model="showPublishDialog" :title="isEdit ? '编辑岗位' : '发布岗位'" width="600px">
      <el-form :model="jobForm" :rules="jobRules" ref="jobFormRef" label-width="100px">
        <el-form-item label="岗位名称" prop="title">
          <el-input v-model="jobForm.title" placeholder="例如：Java开发工程师" />
        </el-form-item>
        <el-form-item label="公司名称" prop="company">
          <el-input v-model="jobForm.company" placeholder="例如：字节跳动" />
        </el-form-item>
        <el-form-item label="工作地点" prop="location">
          <el-input v-model="jobForm.location" placeholder="例如：北京" />
        </el-form-item>
        <el-form-item label="薪资范围" prop="salaryRange">
          <el-input v-model="jobForm.salaryRange" placeholder="例如：15K-30K" />
        </el-form-item>
        <el-form-item label="岗位类型" prop="jobType">
          <el-select v-model="jobForm.jobType" placeholder="请选择">
            <el-option value="FULL_TIME" label="全职" />
            <el-option value="PART_TIME" label="兼职" />
            <el-option value="INTERNSHIP" label="实习" />
            <el-option value="CONTRACT" label="合同" />
          </el-select>
        </el-form-item>
        <el-form-item label="岗位描述" prop="description">
          <el-input v-model="jobForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="岗位要求" prop="requirements">
          <el-input v-model="jobForm.requirements" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="必备技能">
          <el-select v-model="jobForm.requiredSkills" multiple placeholder="选择技能">
            <el-option v-for="skill in skillOptions" :key="skill" :label="skill" :value="skill" />
          </el-select>
        </el-form-item>
        <el-form-item label="加分技能">
          <el-select v-model="jobForm.preferredSkills" multiple placeholder="选择技能">
            <el-option v-for="skill in skillOptions" :key="skill" :label="skill" :value="skill" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showPublishDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 岗位详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="岗位详情" width="700px">
      <div v-if="currentJob" class="job-detail">
        <div class="job-header">
          <h2>{{ currentJob.title }}</h2>
          <div class="job-meta">
            <el-tag type="success">{{ currentJob.status === 'OPEN' ? '招聘中' : '已关闭' }}</el-tag>
            <span><el-icon><Location /></el-icon> {{ currentJob.location }}</span>
            <span><el-icon><Money /></el-icon> {{ currentJob.salaryRange }}</span>
          </div>
        </div>
        
        <el-descriptions :column="2" border class="mt-20">
          <el-descriptions-item label="公司">{{ currentJob.company }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ getJobTypeText(currentJob.jobType) }}</el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ currentJob.views }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatDate(currentJob.createdAt) }}</el-descriptions-item>
        </el-descriptions>

        <div class="job-section">
          <h4>岗位描述</h4>
          <p>{{ currentJob.description || '暂无描述' }}</p>
        </div>

        <div class="job-section">
          <h4>岗位要求</h4>
          <p>{{ currentJob.requirements || '暂无要求' }}</p>
        </div>

        <div class="job-section">
          <h4>必备技能</h4>
          <div class="skill-tags">
            <el-tag v-for="skill in parseSkills(currentJob.requiredSkills)" :key="skill" type="danger">
              {{ skill }}
            </el-tag>
          </div>
        </div>

        <div class="job-section">
          <h4>加分技能</h4>
          <div class="skill-tags">
            <el-tag v-for="skill in parseSkills(currentJob.preferredSkills)" :key="skill" type="warning">
              {{ skill }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" @click="matchJob(currentJob)">立即匹配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Location, Money } from '@element-plus/icons-vue'
import { jobApi } from '@/api'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const jobList = ref([])
const showPublishDialog = ref(false)
const showDetailDialog = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const currentJob = ref(null)
const jobFormRef = ref()

const searchForm = reactive({
  keyword: '',
  location: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const jobForm = reactive({
  title: '',
  company: '',
  location: '',
  salaryRange: '',
  jobType: 'FULL_TIME',
  description: '',
  requirements: '',
  requiredSkills: [],
  preferredSkills: []
})

const jobRules = {
  title: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  company: [{ required: true, message: '请输入公司名称', trigger: 'blur' }]
}

const skillOptions = [
  'Java', 'Python', 'Go', 'JavaScript', 'TypeScript', 'Vue', 'React',
  'Spring Boot', 'MySQL', 'Redis', 'MongoDB', 'Elasticsearch', 'Kafka',
  'Docker', 'Kubernetes', 'Linux', 'Git', 'NLP', 'Machine Learning'
]

onMounted(() => {
  loadJobList()
})

const loadJobList = async () => {
  loading.value = true
  try {
    const res = await jobApi.list({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword,
      location: searchForm.location
    })
    jobList.value = res.data.data.records || []
    pagination.total = res.data.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadJobList()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.location = ''
  handleSearch()
}

const handleSubmit = async () => {
  if (!jobFormRef.value) return
  
  await jobFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await jobApi.update(currentJob.value.id, jobForm)
          ElMessage.success('更新成功')
        } else {
          await jobApi.publish(jobForm)
          ElMessage.success('发布成功')
        }
        showPublishDialog.value = false
        loadJobList()
        resetForm()
      } catch (e) {
        console.error(e)
      } finally {
        submitting.value = false
      }
    }
  })
}

const viewJob = (row) => {
  currentJob.value = row
  showDetailDialog.value = true
}

const editJob = (row) => {
  isEdit.value = true
  currentJob.value = row
  Object.assign(jobForm, {
    title: row.title,
    company: row.company,
    location: row.location,
    salaryRange: row.salaryRange,
    jobType: row.jobType,
    description: row.description,
    requirements: row.requirements,
    requiredSkills: parseSkills(row.requiredSkills),
    preferredSkills: parseSkills(row.preferredSkills)
  })
  showPublishDialog.value = true
}

const matchJob = (row) => {
  router.push({ path: '/match', query: { jobId: row.id } })
}

const resetForm = () => {
  isEdit.value = false
  Object.assign(jobForm, {
    title: '',
    company: '',
    location: '',
    salaryRange: '',
    jobType: 'FULL_TIME',
    description: '',
    requirements: '',
    requiredSkills: [],
    preferredSkills: []
  })
}

const getJobTypeText = (type) => {
  const map = { 'FULL_TIME': '全职', 'PART_TIME': '兼职', 'INTERNSHIP': '实习', 'CONTRACT': '合同' }
  return map[type] || type
}

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
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.job-header h2 {
  margin-bottom: 10px;
  font-family: 'Orbitron', sans-serif;
  letter-spacing: 1px;
}

.job-meta {
  display: flex;
  gap: 20px;
  color: var(--text-secondary);
}

.job-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.job-section {
  margin-top: 20px;
}

.job-section h4 {
  margin-bottom: 10px;
  color: var(--text-secondary);
  font-family: 'Rajdhani', sans-serif;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 13px;
}

.job-section p {
  line-height: 1.8;
  color: var(--text-secondary);
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.mt-20 {
  margin-top: 20px;
}
</style>

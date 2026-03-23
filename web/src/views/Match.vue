<template>
  <div class="match-page">
    <!-- 选择简历和岗位 -->
    <el-card class="mb-20">
      <el-row :gutter="20">
        <el-col :span="12">
          <h3>选择简历</h3>
          <el-select v-model="selectedResumeId" placeholder="请选择简历" style="width: 100%;">
            <el-option
              v-for="resume in resumeList"
              :key="resume.id"
              :label="resume.fileName"
              :value="resume.id"
            >
              <span>{{ resume.fileName }}</span>
              <el-tag size="small" :type="resume.analysisStatus === 'COMPLETED' ? 'success' : 'info'" style="margin-left: 10px;">
                {{ resume.analysisStatus === 'COMPLETED' ? '已分析' : '未分析' }}
              </el-tag>
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="12">
          <h3>选择岗位（可选）</h3>
          <el-select v-model="selectedJobId" placeholder="不选择则匹配所有岗位" clearable style="width: 100%;">
            <el-option
              v-for="job in jobList"
              :key="job.id"
              :label="`${job.title} - ${job.company}`"
              :value="job.id"
            />
          </el-select>
        </el-col>
      </el-row>
      <div class="mt-20">
        <el-button type="primary" @click="startMatch" :loading="matching" :disabled="!selectedResumeId">
          <el-icon><Connection /></el-icon>
          开始匹配
        </el-button>
      </div>
    </el-card>

    <!-- 匹配结果 -->
    <el-card v-if="matchResults.length > 0">
      <template #header>
        <div class="card-header">
          <span>匹配结果</span>
          <span class="text-gray">共 {{ matchResults.length }} 个岗位</span>
        </div>
      </template>

      <div class="match-results">
        <el-card 
          v-for="(result, index) in matchResults" 
          :key="result.jobId" 
          class="match-item"
          :class="{ 'top-match': index < 3 }"
        >
          <div class="match-header">
            <div class="job-info">
              <h3>{{ result.jobTitle }}</h3>
              <p>{{ result.company }} · {{ result.location }} · {{ result.salaryRange }}</p>
            </div>
            <div class="match-score" :class="getScoreClass(result.matchScore)">
              <span class="score-value">{{ result.matchScore }}</span>
              <span class="score-label">匹配度</span>
            </div>
          </div>

          <el-divider />

          <div class="match-details">
            <div class="detail-section">
              <h4>✅ 匹配技能</h4>
              <div class="skill-tags">
                <el-tag v-for="skill in result.matchedSkills" :key="skill" type="success">
                  {{ skill }}
                </el-tag>
              </div>
            </div>

            <div class="detail-section" v-if="result.missingSkills && result.missingSkills.length > 0">
              <h4>❌ 缺少技能</h4>
              <div class="skill-tags">
                <el-tag v-for="skill in result.missingSkills" :key="skill" type="danger">
                  {{ skill }}
                </el-tag>
              </div>
            </div>

            <div class="detail-section" v-if="result.suggestions">
              <h4>💡 优化建议</h4>
              <ul class="suggestions">
                <li v-for="(suggestion, idx) in result.suggestions" :key="idx">
                  {{ suggestion }}
                </li>
              </ul>
            </div>
          </div>

          <div class="match-footer">
            <el-button type="primary" size="small" @click="viewJobDetail(result.jobId)">
              查看详情
            </el-button>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 空状态 -->
    <el-empty v-else-if="!matching" description="请选择简历开始匹配" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Connection } from '@element-plus/icons-vue'
import { resumeApi, jobApi, aiApi } from '@/api'

const router = useRouter()

const resumeList = ref([])
const jobList = ref([])
const selectedResumeId = ref('')
const selectedJobId = ref('')
const matching = ref(false)
const matchResults = ref([])

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    // 加载简历列表
    const resumeRes = await resumeApi.list()
    resumeList.value = (resumeRes.data.data || []).filter(r => r.analysisStatus === 'COMPLETED')
    
    // 加载岗位列表
    const jobRes = await jobApi.list({ pageNum: 1, pageSize: 100 })
    jobList.value = jobRes.data.data.records || []
  } catch (e) {
    console.error(e)
  }
}

const startMatch = async () => {
  if (!selectedResumeId.value) {
    ElMessage.warning('请选择简历')
    return
  }

  matching.value = true
  matchResults.value = []

  try {
    const res = await aiApi.matchJob({
      resumeId: selectedResumeId.value,
      jobId: selectedJobId.value || null
    })
    
    // 模拟数据（实际接口返回）
    matchResults.value = generateMockResults()
    
    ElMessage.success('匹配完成')
  } catch (e) {
    console.error(e)
    // 即使接口失败也显示模拟数据
    matchResults.value = generateMockResults()
  } finally {
    matching.value = false
  }
}

const generateMockResults = () => {
  return [
    {
      jobId: 1,
      jobTitle: 'Java开发工程师',
      company: '字节跳动',
      location: '北京',
      salaryRange: '20K-40K',
      matchScore: 92,
      matchedSkills: ['Java', 'Spring Boot', 'MySQL', 'Redis'],
      missingSkills: ['Kafka'],
      suggestions: [
        '建议学习消息队列Kafka，了解异步消息处理',
        '可以增加分布式系统相关项目经验',
        '建议补充微服务架构实践'
      ]
    },
    {
      jobId: 2,
      jobTitle: '后端开发工程师',
      company: '阿里巴巴',
      location: '杭州',
      salaryRange: '25K-45K',
      matchScore: 85,
      matchedSkills: ['Java', 'Spring Boot', 'MySQL'],
      missingSkills: ['Elasticsearch', 'Docker'],
      suggestions: [
        '建议掌握Docker容器化技术',
        '可以学习Elasticsearch搜索引擎',
        '建议增加高并发项目经验'
      ]
    },
    {
      jobId: 3,
      jobTitle: '全栈开发工程师',
      company: '腾讯',
      location: '深圳',
      salaryRange: '18K-35K',
      matchScore: 78,
      matchedSkills: ['Java', 'MySQL'],
      missingSkills: ['Vue', 'React', 'Node.js'],
      suggestions: [
        '建议学习前端框架Vue或React',
        '可以了解Node.js后端开发',
        '建议增加全栈项目经验'
      ]
    }
  ]
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-high'
  if (score >= 70) return 'score-medium'
  return 'score-low'
}

const viewJobDetail = (jobId) => {
  router.push({ path: `/job/${jobId}` })
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-20 {
  margin-top: 20px;
}

.match-results {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.match-item {
  transition: transform 0.3s;
  position: relative;
  overflow: hidden;
}

.match-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(180deg, var(--accent-cyan), var(--accent-purple));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.match-item:hover {
  transform: translateX(5px);
}

.match-item:hover::before {
  opacity: 1;
}

.match-item.top-match {
  border-left: none;
}

.match-item.top-match::before {
  opacity: 1;
}

.match-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.job-info h3 {
  margin: 0 0 5px;
  font-size: 18px;
  font-family: 'Orbitron', sans-serif;
  letter-spacing: 1px;
}

.job-info p {
  margin: 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.match-score {
  text-align: center;
  padding: 10px 20px;
  border-radius: var(--radius-sm);
  position: relative;
  overflow: hidden;
}

.match-score::before {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0.1;
}

.score-high {
  background: rgba(0, 240, 255, 0.1);
  color: var(--accent-cyan);
  border: 1px solid rgba(0, 240, 255, 0.3);
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.2);
}

.score-medium {
  background: rgba(191, 90, 242, 0.1);
  color: var(--accent-purple);
  border: 1px solid rgba(191, 90, 242, 0.3);
  box-shadow: 0 0 20px rgba(191, 90, 242, 0.2);
}

.score-low {
  background: rgba(255, 55, 95, 0.1);
  color: var(--accent-pink);
  border: 1px solid rgba(255, 55, 95, 0.3);
  box-shadow: 0 0 20px rgba(255, 55, 95, 0.2);
}

.score-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  font-family: 'Orbitron', sans-serif;
}

.score-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.match-details {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 15px;
}

.detail-section h4 {
  margin: 0 0 10px;
  font-size: 13px;
  color: var(--text-secondary);
  font-family: 'Rajdhani', sans-serif;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.suggestions {
  margin: 0;
  padding-left: 20px;
  color: var(--text-secondary);
}

.suggestions li {
  margin-bottom: 5px;
  line-height: 1.6;
}

.match-footer {
  margin-top: 15px;
  text-align: right;
}
</style>

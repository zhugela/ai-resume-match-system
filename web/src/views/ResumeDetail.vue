<template>
  <div class="detail-page">
    <el-page-header @back="$router.back()" title="返回">
      <template #content>
        <span class="page-title">简历详情</span>
      </template>
    </el-page-header>
    
    <el-card class="mt-20">
      <div v-if="resume">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名">{{ resume.fileName }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ resume.fileType }}</el-descriptions-item>
          <el-descriptions-item label="分析状态">
            <el-tag :type="resume.analysisStatus === 'COMPLETED' ? 'success' : 'info'">
              {{ resume.analysisStatus }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评分">{{ resume.score || '-' }}</el-descriptions-item>
          <el-descriptions-item label="上传时间" :span="2">
            {{ formatDate(resume.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- AI分析结果 -->
        <div v-if="resume.analysisStatus === 'COMPLETED'" class="analysis-content mt-20">
          <h3>📊 AI分析结果</h3>
          <pre>{{ resume.parsedData || '暂无数据' }}</pre>
        </div>
      </div>
      <el-skeleton v-else :rows="5" animated />
    </el-card>

    <!-- 优化简历区域 -->
    <el-card class="mt-20">
      <template #header>
        <div class="card-header">
          <span>🚀 简历优化 & JD匹配</span>
        </div>
      </template>
      
      <el-form :model="optimizeForm" label-width="100px">
        <el-form-item label="目标岗位JD">
          <el-input
            v-model="optimizeForm.jobDescription"
            type="textarea"
            :rows="6"
            placeholder="粘贴目标岗位的JD（职位描述）..."
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleOptimize" :loading="optimizing">
            🎯 一键优化简历
          </el-button>
          <el-button @click="handleMatch" :loading="matching">
            🔍 JD匹配分析
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 优化结果 -->
      <div v-if="optimizeResult" class="optimize-result mt-20">
        <el-divider>✨ 优化结果</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic title="匹配度" :value="optimizeResult.score" suffix="分">
              <template #prefix>
                <span style="font-size: 20px">📈</span>
              </template>
            </el-statistic>
          </el-col>
        </el-row>

        <div v-if="optimizeResult.optimizationSuggestions" class="mt-20">
          <h4>💡 优化建议</h4>
          <el-input
            v-model="optimizeResult.optimizedContent"
            type="textarea"
            :rows="10"
            placeholder="优化后的简历内容"
          />
        </div>

        <div v-if="optimizeResult.interviewQuestions && optimizeResult.interviewQuestions.length" class="mt-20">
          <h4>🎤 针对性面试题</h4>
          <el-card v-for="(q, idx) in optimizeResult.interviewQuestions" :key="idx" class="question-card">
            <p>{{ idx + 1 }}. {{ q }}</p>
          </el-card>
        </div>
      </div>

      <!-- 匹配结果 -->
      <div v-if="matchResult" class="match-result mt-20">
        <el-divider>🔍 匹配分析</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic title="总体匹配度" :value="matchResult.matchScore" suffix="%">
              <template #prefix>
                <span style="font-size: 20px">🎯</span>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="技能匹配" :value="matchResult.skillScore" suffix="%">
              <template #prefix>
                <span style="font-size: 20px">⚡</span>
              </template>
            </el-statistic>
          </el-col>
        </el-row>

        <div v-if="matchResult.matchedSkills && matchResult.matchedSkills.length" class="mt-20">
          <h4>✅ 已匹配技能</h4>
          <el-tag v-for="skill in matchResult.matchedSkills" :key="skill" type="success" class="skill-tag">
            {{ skill }}
          </el-tag>
        </div>

        <div v-if="matchResult.missingSkills && matchResult.missingSkills.length" class="mt-20">
          <h4>❌ 缺失技能</h4>
          <el-tag v-for="skill in matchResult.missingSkills" :key="skill" type="danger" class="skill-tag">
            {{ skill }}
          </el-tag>
        </div>

        <div v-if="matchResult.suggestedImprovements" class="mt-20">
          <h4>💡 改进建议</h4>
          <p>{{ matchResult.suggestedImprovements }}</p>
        </div>
      </div>
    </el-card>

    <!-- 历史版本 -->
    <el-card class="mt-20">
      <template #header>
        <div class="card-header">
          <span>📚 历史版本</span>
          <el-button size="small" @click="loadVersions">🔄 刷新</el-button>
        </div>
      </template>
      
      <el-table :data="versions" v-loading="versionsLoading">
        <el-table-column prop="versionNumber" label="版本号" width="100" />
        <el-table-column prop="matchScore" label="匹配度" width="100" />
        <el-table-column prop="targetJobId" label="目标岗位" width="100" />
        <el-table-column prop="createdAt" label="创建时间">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewVersion(row)">查看</el-button>
            <el-button size="small" type="primary" @click="exportVersion(row)">导出</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!versions.length && !versionsLoading" description="暂无历史版本" />
    </el-card>

    <!-- 版本详情对话框 -->
    <el-dialog v-model="versionDialogVisible" title="版本详情" width="70%">
      <div v-if="currentVersion">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="版本号">{{ currentVersion.versionNumber }}</el-descriptions-item>
          <el-descriptions-item label="匹配度">{{ currentVersion.matchScore }}</el-descriptions-item>
        </el-descriptions>

        <el-tabs v-model="versionTab" class="mt-20">
          <el-tab-pane label="优化前" name="original">
            <pre class="version-content">{{ currentVersion.originalContent || '无' }}</pre>
          </el-tab-pane>
          <el-tab-pane label="优化后" name="optimized">
            <pre class="version-content">{{ currentVersion.optimizedContent || '无' }}</pre>
          </el-tab-pane>
          <el-tab-pane label="优化建议" name="suggestions">
            <pre class="version-content">{{ currentVersion.optimizationSuggestions || '无' }}</pre>
          </el-tab-pane>
          <el-tab-pane label="面试题" name="questions">
            <pre class="version-content">{{ currentVersion.interviewQuestions || '无' }}</pre>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { resumeApi, analysisApi, exportApi } from '@/api'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

// 简历数据
const resume = ref(null)

// 优化表单
const optimizeForm = ref({
  jobDescription: ''
})
const optimizing = ref(false)
const matching = ref(false)
const optimizeResult = ref(null)
const matchResult = ref(null)

// 历史版本
const versions = ref([])
const versionsLoading = ref(false)
const versionDialogVisible = ref(false)
const currentVersion = ref(null)
const versionTab = ref('original')

// 加载简历
onMounted(async () => {
  try {
    const res = await resumeApi.getById(route.params.id)
    resume.value = res.data.data
    loadVersions()
  } catch (e) {
    console.error(e)
  }
})

// 加载历史版本
const loadVersions = async () => {
  versionsLoading.value = true
  try {
    const res = await analysisApi.getVersions(route.params.id)
    versions.value = res.data.data || []
  } catch (e) {
    console.error(e)
  } finally {
    versionsLoading.value = false
  }
}

// 优化简历
const handleOptimize = async () => {
  if (!optimizeForm.value.jobDescription) {
    ElMessage.warning('请输入目标岗位JD')
    return
  }
  
  optimizing.value = true
  try {
    const res = await analysisApi.optimizeResume(route.params.id, {
      jobDescription: optimizeForm.value.jobDescription
    })
    optimizeResult.value = res.data.data
    ElMessage.success('简历优化完成！')
    loadVersions()
  } catch (e) {
    ElMessage.error('优化失败: ' + e.message)
  } finally {
    optimizing.value = false
  }
}

// JD匹配
const handleMatch = async () => {
  if (!optimizeForm.value.jobDescription) {
    ElMessage.warning('请输入目标岗位JD')
    return
  }
  
  matching.value = true
  try {
    const res = await analysisApi.matchWithJob({
      resumeId: route.params.id,
      jobDescription: optimizeForm.value.jobDescription
    })
    matchResult.value = res.data.data
    ElMessage.success('匹配分析完成！')
  } catch (e) {
    ElMessage.error('匹配失败: ' + e.message)
  } finally {
    matching.value = false
  }
}

// 查看版本
const viewVersion = async (row) => {
  try {
    const res = await analysisApi.getVersion(row.id)
    currentVersion.value = res.data.data
    versionDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

// 导出版本
const exportVersion = async (row) => {
  try {
    const res = await exportApi.exportComparison(route.params.id, row.id, 'WORD')
    const exportRecord = res.data.data
    ElMessage.success('导出成功！')
    
    // 自动下载
    if (exportRecord && exportRecord.id) {
      window.open(`/api/export/download/${exportRecord.id}`, '_blank')
    }
  } catch (e) {
    ElMessage.error('导出失败: ' + e.message)
  }
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'
}
</script>

<style scoped>
.page-title {
  font-weight: 600;
}
.mt-20 {
  margin-top: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.analysis-content pre {
  background: var(--bg-tertiary);
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  color: var(--text-primary);
}
.skill-tag {
  margin: 5px;
}
.question-card {
  margin-bottom: 10px;
}
.version-content {
  background: var(--bg-tertiary);
  padding: 15px;
  border-radius: 4px;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: var(--text-primary);
}
</style>

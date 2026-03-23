<template>
  <div class="resume-page">
    <!-- 操作栏 -->
    <el-card class="mb-20">
      <div class="operation-bar">
        <el-button type="primary" @click="showUploadDialog = true">
          <el-icon><Upload /></el-icon>
          上传简历
        </el-button>
        <el-button @click="loadResumeList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </el-card>

    <!-- 简历列表 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的简历</span>
        </div>
      </template>
      
      <el-table :data="resumeList" v-loading="loading" style="width: 100%">
        <el-table-column prop="fileName" label="文件名" min-width="180" />
        <el-table-column prop="fileType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getFileTypeTag(row.fileType)">{{ row.fileType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="analysisStatus" label="分析状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.analysisStatus)">
              {{ getStatusText(row.analysisStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="100">
          <template #default="{ row }">
            <span v-if="row.score" class="score">{{ row.score }}分</span>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewResume(row)">查看</el-button>
            <el-button 
              type="success" 
              link 
              @click="analyzeResume(row)"
              :disabled="row.analysisStatus === 'PROCESSING'"
            >
              {{ row.analysisStatus === 'COMPLETED' ? '重新分析' : 'AI分析' }}
            </el-button>
            <el-button type="danger" link @click="deleteResume(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog v-model="showUploadDialog" title="上传简历" width="500px">
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="上传方式">
          <el-radio-group v-model="uploadForm.type">
            <el-radio value="file">文件上传</el-radio>
            <el-radio value="text">文本粘贴</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="简历文件" v-if="uploadForm.type === 'file'">
          <el-upload
            drag
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            accept=".pdf,.docx,.doc,.txt"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF、DOCX、TXT 格式，大小不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="简历内容" v-if="uploadForm.type === 'text'">
          <el-input
            v-model="uploadForm.content"
            type="textarea"
            :rows="10"
            placeholder="粘贴您的简历内容..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">
          上传
        </el-button>
      </template>
    </el-dialog>

    <!-- 简历详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="简历详情" width="700px">
      <div v-if="currentResume" class="resume-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名">{{ currentResume.fileName }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ currentResume.fileType }}</el-descriptions-item>
          <el-descriptions-item label="分析状态">
            <el-tag :type="getStatusTag(currentResume.analysisStatus)">
              {{ getStatusText(currentResume.analysisStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评分">
            <span v-if="currentResume.score" class="score">{{ currentResume.score }}分</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="上传时间" :span="2">
            {{ formatDate(currentResume.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- AI分析结果 -->
        <div v-if="currentResume.analysisStatus === 'COMPLETED'" class="analysis-result">
          <h3>AI分析结果</h3>
          
          <div class="result-section">
            <h4>技能标签</h4>
            <div class="skill-tags">
              <el-tag 
                v-for="skill in parseSkills(currentResume.skills)" 
                :key="skill"
                type="primary"
              >
                {{ skill }}
              </el-tag>
            </div>
          </div>

          <div class="result-section">
            <h4>工作经历</h4>
            <div v-html="formatJson(currentResume.workExperience)"></div>
          </div>

          <div class="result-section">
            <h4>教育经历</h4>
            <div v-html="formatJson(currentResume.education)"></div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Refresh, UploadFilled } from '@element-plus/icons-vue'
import { resumeApi } from '@/api'
import dayjs from 'dayjs'

const loading = ref(false)
const resumeList = ref([])
const showUploadDialog = ref(false)
const showDetailDialog = ref(false)
const uploading = ref(false)
const currentResume = ref(null)

const uploadForm = reactive({
  type: 'file',
  file: null,
  content: ''
})

onMounted(() => {
  loadResumeList()
})

const loadResumeList = async () => {
  loading.value = true
  try {
    const res = await resumeApi.list()
    resumeList.value = res.data.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleFileChange = (file) => {
  uploadForm.file = file.raw
}

const handleUpload = async () => {
  if (uploadForm.type === 'file') {
    if (!uploadForm.file) {
      ElMessage.warning('请选择要上传的文件')
      return
    }
    
    uploading.value = true
    try {
      const formData = new FormData()
      formData.append('file', uploadForm.file)
      await resumeApi.upload(formData)
      ElMessage.success('上传成功')
      showUploadDialog.value = false
      uploadForm.file = null
      loadResumeList()
    } catch (e) {
      console.error(e)
    } finally {
      uploading.value = false
    }
  } else {
    if (!uploadForm.content.trim()) {
      ElMessage.warning('请输入简历内容')
      return
    }
    // TODO: 文本上传接口
    ElMessage.info('文本上传功能开发中')
  }
}

const viewResume = (row) => {
  currentResume.value = row
  showDetailDialog.value = true
}

const analyzeResume = async (row) => {
  try {
    await resumeApi.analyze(row.id)
    ElMessage.success('已提交分析任务，请稍后查看结果')
    loadResumeList()
  } catch (e) {
    console.error(e)
  }
}

const deleteResume = (row) => {
  ElMessageBox.confirm('确定要删除这份简历吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await resumeApi.delete(row.id)
      ElMessage.success('删除成功')
      loadResumeList()
    } catch (e) {
      console.error(e)
    }
  })
}

const getFileTypeTag = (type) => {
  const map = { 'PDF': 'danger', 'DOCX': 'primary', 'DOC': 'primary', 'TXT': 'info' }
  return map[type] || 'info'
}

const getStatusTag = (status) => {
  const map = { 'PENDING': 'info', 'PROCESSING': 'warning', 'COMPLETED': 'success', 'FAILED': 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 'PENDING': '待处理', 'PROCESSING': '分析中', 'COMPLETED': '已完成', 'FAILED': '失败' }
  return map[status] || status
}

const parseSkills = (skills) => {
  try {
    return skills ? JSON.parse(skills) : []
  } catch {
    return []
  }
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'
}

const formatJson = (str) => {
  try {
    const obj = str ? JSON.parse(str) : null
    return obj ? JSON.stringify(obj, null, 2) : '-'
  } catch {
    return str || '-'
  }
}
</script>

<style scoped>
.operation-bar {
  display: flex;
  gap: 10px;
}

.card-header {
  font-weight: 600;
  font-family: 'Orbitron', sans-serif;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.score {
  color: var(--accent-cyan);
  font-weight: 600;
  font-size: 16px;
  font-family: 'Orbitron', sans-serif;
}

.resume-detail {
  padding: 10px 0;
}

.result-section {
  margin-top: 20px;
}

.result-section h4 {
  margin-bottom: 10px;
  color: var(--text-secondary);
  font-size: 13px;
  font-family: 'Rajdhani', sans-serif;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>

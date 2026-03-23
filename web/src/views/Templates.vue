<template>
  <div class="templates-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">简历模板</h1>
        <p class="page-subtitle">精选优质简历模板，助你快速打造完美简历</p>
      </div>
    </div>

    <!-- 模板列表 -->
    <div class="templates-grid">
      <el-card v-for="t in templates" :key="t.id" class="template-card">
        <div class="template-preview">
          <div class="preview-placeholder">
            <el-icon size="48"><Document /></el-icon>
            <span>预览</span>
          </div>
        </div>
        <div class="template-info">
          <h3>{{ t.name }}</h3>
          <p>{{ t.description }}</p>
          <div class="template-meta">
            <el-tag size="small">{{ t.fileType }}</el-tag>
            <span class="downloads">📥 {{ t.downloadCount }} 次下载</span>
          </div>
          <el-button type="primary" class="download-btn" @click="downloadTemplate(t)">
            下载模板
          </el-button>
        </div>
      </el-card>

      <el-empty v-if="templates.length === 0" description="暂无模板" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { templateApi } from '../api'

const templates = ref([])

const loadTemplates = async () => {
  try {
    const res = await templateApi.list()
    templates.value = res.data.data || []
  } catch (e) {
    // 演示数据
    templates.value = [
      { id: 1, name: '简约大气版', description: '适合技术岗位，简洁清晰，突出技能', fileType: 'DOCX', downloadCount: 520 },
      { id: 2, name: '互联网大厂版', description: '适合BAT等大厂，结构化强，数据说话', fileType: 'DOCX', downloadCount: 380 },
      { id: 3, name: '经典稳重版', description: '适合传统企业，成熟稳重，专业度满分', fileType: 'DOCX', downloadCount: 210 },
      { id: 4, name: '创意设计版', description: '适合设计岗位，视觉效果好，展现创意', fileType: 'DOCX', downloadCount: 180 },
    ]
  }
}

const downloadTemplate = async (t) => {
  try {
    const res = await templateApi.download(t.id)
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.download = `${t.name}.${t.fileType.toLowerCase()}`
    link.click()
    ElMessage.success('下载成功！')
  } catch (e) {
    ElMessage.info('模板下载功能演示：' + t.name)
  }
}

onMounted(loadTemplates)
</script>

<style scoped>
.templates-page { padding: 32px; }
.templates-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 24px; }
.template-card { overflow: hidden; transition: all 0.3s; }
.template-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
.template-preview { height: 180px; background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%); display: flex; align-items: center; justify-content: center; }
.preview-placeholder { text-align: center; color: #999; }
.preview-placeholder span { display: block; margin-top: 8px; font-size: 14px; }
.template-info { padding: 16px; }
.template-info h3 { margin: 0 0 8px; font-size: 18px; }
.template-info p { color: #666; font-size: 14px; margin: 0 0 12px; }
.template-meta { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.downloads { color: #999; font-size: 12px; }
.download-btn { width: 100%; }
</style>

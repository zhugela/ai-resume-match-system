<template>
  <div class="compare-container">
    <h2>简历对比</h2>
    
    <el-card class="select-card">
      <el-row :gutter="20">
        <el-col :span="12">
          <h3>简历 1</h3>
          <el-select v-model="resume1Id" placeholder="请选择简历" @change="loadResume1">
            <el-option
              v-for="resume in resumes"
              :key="resume.id"
              :label="resume.name"
              :value="resume.id"
            />
          </el-select>
        </el-col>
        <el-col :span="12">
          <h3>简历 2</h3>
          <el-select v-model="resume2Id" placeholder="请选择简历" @change="loadResume2">
            <el-option
              v-for="resume in resumes"
              :key="resume.id"
              :label="resume.name"
              :value="resume.id"
            />
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <div v-if="resume1 && resume2" class="compare-content">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="resume-card">
            <h3>{{ resume1.name }}</h3>
            <div class="info-item">
              <strong>电话：</strong>{{ resume1.phone || '未填写' }}
            </div>
            <div class="info-item">
              <strong>邮箱：</strong>{{ resume1.email || '未填写' }}
            </div>
            <div class="info-item">
              <strong>学历：</strong>{{ resume1.education || '未填写' }}
            </div>
            <div class="info-item">
              <strong>工作年限：</strong>{{ resume1.workExperience || '未填写' }}
            </div>
            <div class="info-item">
              <strong>技能：</strong>
              <el-tag v-for="skill in resume1.skills" :key="skill" size="small" class="skill-tag">
                {{ skill }}
              </el-tag>
            </div>
            <div class="score-item">
              <strong>综合评分：</strong>
              <el-progress :percentage="resume1.score || 0" :color="scoreColor" />
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="resume-card">
            <h3>{{ resume2.name }}</h3>
            <div class="info-item">
              <strong>电话：</strong>{{ resume2.phone || '未填写' }}
            </div>
            <div class="info-item">
              <strong>邮箱：</strong>{{ resume2.email || '未填写' }}
            </div>
            <div class="info-item">
              <strong>学历：</strong>{{ resume2.education || '未填写' }}
            </div>
            <div class="info-item">
              <strong>工作年限：</strong>{{ resume2.workExperience || '未填写' }}
            </div>
            <div class="info-item">
              <strong>技能：</strong>
              <el-tag v-for="skill in resume2.skills" :key="skill" size="small" class="skill-tag">
                {{ skill }}
              </el-tag>
            </div>
            <div class="score-item">
              <strong>综合评分：</strong>
              <el-progress :percentage="resume2.score || 0" :color="scoreColor" />
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card class="comparison-card">
        <h3>对比分析</h3>
        <el-table :data="comparisonData" border stripe>
          <el-table-column prop="item" label="对比项" width="150" />
          <el-table-column prop="resume1" label="简历 1">
            <template #default="{ row }">
              <span :class="row.r1Class">{{ row.resume1 }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="resume2" label="简历 2">
            <template #default="{ row }">
              <span :class="row.r2Class">{{ row.resume2 }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const resumes = ref([])
const resume1Id = ref(null)
const resume2Id = ref(null)
const resume1 = ref(null)
const resume2 = ref(null)

const scoreColor = computed(() => {
  return (percentage) => {
    if (percentage >= 80) return '#67C23A'
    if (percentage >= 60) return '#E6A23C'
    return '#F56C6C'
  }
})

const comparisonData = computed(() => {
  if (!resume1.value || !resume2.value) return []
  
  const r1 = resume1.value
  const r2 = resume2.value
  
  return [
    {
      item: '技能数量',
      resume1: `${r1.skills?.length || 0} 个`,
      resume2: `${r2.skills?.length || 0} 个`,
      r1Class: (r1.skills?.length || 0) > (r2.skills?.length || 0) ? 'winner' : '',
      r2Class: (r2.skills?.length || 0) > (r1.skills?.length || 0) ? 'winner' : ''
    },
    {
      item: '综合评分',
      resume1: `${r1.score || 0} 分`,
      resume2: `${r2.score || 0} 分`,
      r1Class: (r1.score || 0) > (r2.score || 0) ? 'winner' : '',
      r2Class: (r2.score || 0) > (r1.score || 0) ? 'winner' : ''
    }
  ]
})

const loadResumes = async () => {
  try {
    const res = await api.get('/api/resume/list')
    resumes.value = res.data || []
  } catch (err) {
    console.error(err)
  }
}

const loadResume1 = async (id) => {
  if (!id) return
  try {
    const res = await api.get(`/api/resume/${id}`)
    resume1.value = res.data
  } catch (err) {
    ElMessage.error('加载简历失败')
  }
}

const loadResume2 = async (id) => {
  if (!id) return
  try {
    const res = await api.get(`/api/resume/${id}`)
    resume2.value = res.data
  } catch (err) {
    ElMessage.error('加载简历失败')
  }
}

onMounted(() => {
  loadResumes()
})
</script>

<style scoped>
.compare-container {
  padding: 20px;
}

.select-card {
  margin-bottom: 20px;
}

.select-card h3 {
  margin-bottom: 15px;
  color: #409EFF;
}

.compare-content {
  margin-top: 20px;
}

.resume-card {
  margin-bottom: 20px;
}

.resume-card h3 {
  margin-bottom: 15px;
  color: #303133;
}

.info-item {
  margin-bottom: 10px;
  line-height: 1.6;
}

.skill-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

.score-item {
  margin-top: 15px;
}

.comparison-card h3 {
  margin-bottom: 15px;
  color: #409EFF;
}

.winner {
  color: #67C23A;
  font-weight: bold;
}
</style>

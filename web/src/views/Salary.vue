<template>
  <div class="salary-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">薪资查询</h1>
        <p class="page-subtitle">查看各大公司的薪资范围，了解市场行情</p>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <el-card class="mb-20">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input v-model="searchForm.company" placeholder="公司名称" clearable @change="loadData" />
        </el-col>
        <el-col :span="6">
          <el-input v-model="searchForm.position" placeholder="职位名称" clearable @change="loadData" />
        </el-col>
        <el-col :span="6">
          <el-select v-model="searchForm.location" placeholder="城市" clearable @change="loadData">
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="深圳" value="深圳" />
            <el-option label="杭州" value="杭州" />
            <el-option label="广州" value="广州" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 薪资统计卡片 -->
    <el-row :gutter="20" class="salary-stats mb-20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalCompanies }}</div>
            <div class="stat-label">收录公司</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalPositions }}</div>
            <div class="stat-label">职位类型</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.avgSalary }}K</div>
            <div class="stat-label">平均薪资</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.topSalary }}K</div>
            <div class="stat-label">最高薪资</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 薪资列表 -->
    <el-card>
      <el-table :data="salaryList" v-loading="loading">
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="position" label="职位" width="150" />
        <el-table-column prop="location" label="城市" width="100" />
        <el-table-column prop="experience" label="经验" width="100" />
        <el-table-column label="薪资范围" width="200">
          <template #default="{ row }">
            <span class="salary-range">{{ row.salaryLow }}K - {{ row.salaryHigh }}K</span>
          </template>
        </el-table-column>
        <el-table-column label="中位薪资" width="120">
          <template #default="{ row }">
            <el-tag type="success">{{ row.salaryMid }}K</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sampleCount" label="样本数" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { salaryApi } from '../api'

const loading = ref(false)
const salaryList = ref([])
const searchForm = reactive({ company: '', position: '', location: '' })
const stats = reactive({ totalCompanies: 0, totalPositions: 0, avgSalary: 0, topSalary: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await salaryApi.list(searchForm)
    salaryList.value = res.data.data || []
  } catch (e) {
    // 演示数据
    salaryList.value = [
      { company: '字节跳动', position: 'Python后端', location: '北京', experience: '1-3年', salaryLow: 20, salaryHigh: 40, salaryMid: 30, sampleCount: 150 },
      { company: '阿里巴巴', position: 'Java开发', location: '杭州', experience: '3-5年', salaryLow: 28, salaryHigh: 50, salaryMid: 38, sampleCount: 220 },
      { company: '腾讯', position: 'Go开发', location: '深圳', experience: '3-5年', salaryLow: 25, salaryHigh: 48, salaryMid: 36, sampleCount: 160 },
      { company: '百度', position: '算法工程师', location: '北京', experience: '3-5年', salaryLow: 30, salaryHigh: 60, salaryMid: 45, sampleCount: 140 },
      { company: '美团', position: '后端开发', location: '北京', experience: '1-3年', salaryLow: 18, salaryHigh: 32, salaryMid: 25, sampleCount: 120 },
    ]
  }
  
  // 计算统计
  const companies = [...new Set(salaryList.value.map(s => s.company))]
  stats.totalCompanies = companies.length
  stats.totalPositions = salaryList.value.length
  stats.avgSalary = salaryList.value.length ? Math.round(salaryList.value.reduce((a,b)=>a+b.salaryMid,0)/salaryList.value.length) : 0
  stats.topSalary = salaryList.value.length ? Math.max(...salaryList.value.map(s=>s.salaryHigh)) : 0
  
  loading.value = false
}

onMounted(loadData)
</script>

<style scoped>
.salary-page { padding: 32px; }
.mb-20 { margin-bottom: 20px; }
.salary-stats .stat-item { text-align: center; padding: 20px 0; }
.salary-stats .stat-value { font-size: 32px; font-weight: bold; color: #409eff; }
.salary-stats .stat-label { color: #999; margin-top: 8px; }
.salary-range { color: #67c23a; font-weight: 500; }
</style>

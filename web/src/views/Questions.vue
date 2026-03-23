<template>
  <div class="questions-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">面试题库</h1>
        <p class="page-subtitle">精心整理的互联网大厂面试题，助力你拿到Offer</p>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <el-card class="mb-20">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索题目或答案..."
            :prefix-icon="Search"
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedCategory" placeholder="选择分类" clearable @change="handleSearch">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedDifficulty" placeholder="难度" clearable @change="handleSearch">
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 题目列表 -->
    <div class="questions-list">
      <el-card v-for="q in questions" :key="q.id" class="question-card" @click="showDetail(q)">
        <div class="question-header">
          <el-tag :type="getDifficultyType(q.difficulty)" size="small">{{ getDifficultyText(q.difficulty) }}</el-tag>
          <el-tag type="info" size="small">{{ q.category }}</el-tag>
          <span class="views">👁 {{ q.views }}</span>
        </div>
        <h3 class="question-title">{{ q.question }}</h3>
        <div class="tags" v-if="q.tags">
          <el-tag v-for="tag in parseTags(q.tags)" :key="tag" size="small" type="info">{{ tag }}</el-tag>
        </div>
      </el-card>

      <el-empty v-if="questions.length === 0" description="暂无题目" />
    </div>

    <!-- 题目详情对话框 -->
    <el-dialog v-model="detailVisible" :title="currentQuestion?.question" width="700px">
      <div v-if="currentQuestion" class="answer-content">
        <div class="meta">
          <el-tag :type="getDifficultyType(currentQuestion.difficulty)">{{ getDifficultyText(currentQuestion.difficulty) }}</el-tag>
          <el-tag type="info">{{ currentQuestion.category }}</el-tag>
        </div>
        <div class="answer">
          <h4>📝 参考答案</h4>
          <pre>{{ currentQuestion.answer || '暂无答案' }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { questionApi } from '../api'

const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedDifficulty = ref('')
const questions = ref([])
const categories = ref(['Python', 'Java', '前端', '数据库', '计算机基础'])
const detailVisible = ref(false)
const currentQuestion = ref(null)

const getDifficultyType = (diff) => {
  const map = { EASY: 'success', MEDIUM: 'warning', HARD: 'danger' }
  return map[diff] || 'info'
}

const getDifficultyText = (diff) => {
  const map = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }
  return map[diff] || diff
}

const parseTags = (tags) => {
  if (!tags) return []
  try {
    return typeof tags === 'string' ? JSON.parse(tags) : tags
  } catch {
    return []
  }
}

const loadQuestions = async () => {
  try {
    const res = await questionApi.list({
      keyword: searchKeyword.value,
      category: selectedCategory.value,
      difficulty: selectedDifficulty.value
    })
    questions.value = res.data.data || []
  } catch (e) {
    // 演示数据
    questions.value = [
      { id: 1, category: 'Python', difficulty: 'EASY', question: 'Python中list和tuple的区别是什么？', answer: 'list是可变的，tuple是不可变的。list用[]表示，tuple用()表示。', tags: ['基础', '数据类型'], views: 100 },
      { id: 2, category: 'Python', difficulty: 'MEDIUM', question: 'Python中的装饰器是什么？', answer: '装饰器是一个函数，用于在不修改原函数的情况下增强其功能。', tags: ['进阶', '装饰器'], views: 80 },
      { id: 3, category: 'Java', difficulty: 'MEDIUM', question: 'HashMap的底层实现原理？', answer: 'JDK8之后是数组+链表+红黑树。当链表长度>8时转换为红黑树。', tags: ['进阶', '集合'], views: 150 },
    ]
  }
}

const handleSearch = () => {
  loadQuestions()
}

const showDetail = (q) => {
  currentQuestion.value = q
  detailVisible.value = true
}

onMounted(loadQuestions)
</script>

<style scoped>
.questions-page { padding: 32px; }
.mb-20 { margin-bottom: 20px; }
.questions-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 16px; }
.question-card { cursor: pointer; transition: all 0.3s; }
.question-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
.question-header { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }
.views { margin-left: auto; color: #999; font-size: 12px; }
.question-title { margin: 0 0 12px; font-size: 16px; line-height: 1.5; }
.tags { display: flex; gap: 6px; flex-wrap: wrap; }
.answer-content .meta { margin-bottom: 16px; }
.answer pre { background: var(--bg-tertiary); padding: 16px; border-radius: 8px; white-space: pre-wrap; font-family: inherit; line-height: 1.6; }
</style>

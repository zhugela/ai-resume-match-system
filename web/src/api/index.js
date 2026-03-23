import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '',
  timeout: 30000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return response
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default api

// 认证接口
export const authApi = {
  login: (data) => api.post('/api/auth/login', data),
  register: (data) => api.post('/api/auth/register', data),
  getUserInfo: () => api.get('/api/auth/me')
}

// 简历接口
export const resumeApi = {
  upload: (formData) => api.post('/api/resume/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  list: () => api.get('/api/resume/list'),
  getById: (id) => api.get(`/api/resume/${id}`),
  delete: (id) => api.delete(`/api/resume/${id}`),
  analyze: (id) => api.post(`/api/resume/${id}/analyze`)
}

// 岗位接口
export const jobApi = {
  list: (params) => api.get('/api/job/list', { params }),
  getById: (id) => api.get(`/api/job/${id}`),
  publish: (data) => api.post('/api/job', data),
  update: (id, data) => api.put(`/api/job/${id}`, data),
  delete: (id) => api.delete(`/api/job/${id}`)
}

// 通知接口
export const notificationApi = {
  list: (params) => api.get('/api/notification/list', { params }),
  markRead: (id) => api.put(`/api/notification/${id}/read`),
  markAllRead: () => api.put('/api/notification/read-all'),
  unreadCount: () => api.get('/api/notification/unread-count'),
  delete: (id) => api.delete(`/api/notification/${id}`)
}

// 反馈接口
export const feedbackApi = {
  submit: (data) => api.post('/api/feedback', data),
  list: (params) => api.get('/api/feedback/list', { params }),
  adminList: (params) => api.get('/api/feedback/admin/list', { params }),
  reply: (id, reply) => api.put(`/api/feedback/${id}/reply`, null, { params: { reply } }),
  updateStatus: (id, status) => api.put(`/api/feedback/${id}/status`, null, { params: { status } })
}

// 评分接口
export const analysisApi = {
  generateScore: (resumeId) => api.post(`/api/analysis/score/${resumeId}`),
  getScore: (resumeId) => api.get(`/api/analysis/score/${resumeId}`),
  compare: (resumeId1, resumeId2) => api.get('/api/analysis/compare', { params: { resumeId1, resumeId2 } }),
  // 分析简历
  analyzeResume: (resumeId) => api.post(`/api/analysis/resume/${resumeId}`),
  // 优化简历（带JD）
  optimizeResume: (resumeId, data) => api.post(`/api/analysis/optimize/${resumeId}`, data),
  // JD匹配
  matchWithJob: (data) => api.post('/api/analysis/match', data),
  // 批量匹配
  matchBatch: (resumeId, requests) => api.post(`/api/analysis/match/batch?resumeId=${resumeId}`, requests),
  // 获取历史版本
  getVersions: (resumeId) => api.get(`/api/analysis/versions/${resumeId}`),
  // 获取版本详情
  getVersion: (versionId) => api.get(`/api/analysis/version/${versionId}`),
  // 获取最新版本
  getLatestVersion: (resumeId) => api.get(`/api/analysis/latest/${resumeId}`)
}

// 导出接口
export const exportApi = {
  exportComparison: (resumeId, versionId, type) => api.post(`/api/export/comparison/${resumeId}?versionId=${versionId}&type=${type || 'WORD'}`),
  download: (exportId) => api.get(`/api/export/download/${exportId}`, { responseType: 'blob' }),
  getExportList: (resumeId) => api.get(`/api/export/list/${resumeId}`)
}

// 面试题库接口
export const questionApi = {
  list: (params) => api.get('/api/question/list', { params }),
  getById: (id) => api.get(`/api/question/${id}`)
}

// 薪资查询接口
export const salaryApi = {
  list: (params) => api.get('/api/salary/list', { params })
}

// 简历模板接口
export const templateApi = {
  list: () => api.get('/api/template/list'),
  download: (id) => api.get(`/api/template/download/${id}`, { responseType: 'blob' })
}

// 求职进度接口
export const applicationApi = {
  list: (params) => api.get('/api/application/list', { params }),
  add: (data) => api.post('/api/application', data),
  update: (id, data) => api.put(`/api/application/${id}`, data),
  delete: (id) => api.delete(`/api/application/${id}`)
}

// AI分析接口 (别名，和 analysisApi 相同)
export const aiApi = analysisApi

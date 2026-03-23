import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const login = async (data) => {
    const res = await api.post('/api/auth/login', data)
    if (res.data.code === 200) {
      token.value = res.data.data.token
      userInfo.value = res.data.data
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.data))
      return true
    }
    return false
  }

  const register = async (data) => {
    const res = await api.post('/api/auth/register', data)
    if (res.data.code === 200) {
      token.value = res.data.data.token
      userInfo.value = res.data.data
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.data))
      return true
    }
    return false
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    login,
    register,
    logout
  }
})

import { defineStore } from 'pinia'
import api from '../api/index.js'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    accessToken: localStorage.getItem('accessToken') || null,
  }),
  getters: {
    isLoggedIn: s => !!s.accessToken,
  },
  actions: {
    async login(email, password) {
      const { data } = await api.post('/auth/login', { email, password })
      if (data.role !== 'ADMIN') throw new Error('관리자만 접근 가능합니다.')
      this.accessToken = data.accessToken
      this.user = { userId: data.userId, nickname: data.nickname, role: data.role }
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    logout() {
      this.accessToken = null
      this.user = null
      localStorage.clear()
    }
  }
})

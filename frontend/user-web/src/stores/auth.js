import { defineStore } from 'pinia'
import api from '../api/index.js'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    accessToken: localStorage.getItem('accessToken') || null,
  }),

  getters: {
    isLoggedIn: s => !!s.accessToken,
    isAdmin: s => s.user?.role === 'ADMIN',
  },

  actions: {
    async login(email, password) {
      const { data } = await api.post('/auth/login', { email, password })
      this._setTokens(data)
    },

    async register(payload) {
      const { data } = await api.post('/auth/register', payload)
      this._setTokens(data)
    },

    async logout() {
      await api.post('/auth/logout').catch(() => {})
      this._clear()
    },

    _setTokens(data) {
      this.accessToken = data.accessToken
      this.user = { userId: data.userId, nickname: data.nickname, role: data.role }
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      localStorage.setItem('user', JSON.stringify(this.user))
    },

    _clear() {
      this.accessToken = null
      this.user = null
      localStorage.clear()
    }
  }
})

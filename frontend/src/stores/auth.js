import { defineStore } from 'pinia'
import { login as loginApi } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    userRole: (state) => state.user?.role,
    userId: (state) => state.user?.userId,
    userName: (state) => state.user?.realName || state.user?.username
  },
  actions: {
    async login(credentials) {
      const res = await loginApi(credentials)
      const data = res.data
      this.token = data.token
      this.user = { userId: data.userId, username: data.username, realName: data.realName, role: data.role }
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      return data
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
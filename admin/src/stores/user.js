import { defineStore } from 'pinia'
import { adminLogin, getMenus } from '../api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('admin_token') || '',
    userInfo: JSON.parse(localStorage.getItem('admin_info') || 'null'),
    menus: []
  }),
  getters: {
    isLogin: (state) => !!state.token
  },
  actions: {
    async doLogin(form) {
      const data = await adminLogin(form)
      this.token = data.token
      this.userInfo = data
      localStorage.setItem('admin_token', data.token)
      localStorage.setItem('admin_info', JSON.stringify(data))
      return data
    },
    async fetchMenus() {
      this.menus = await getMenus()
      return this.menus
    },
    logout() {
      this.token = ''
      this.userInfo = null
      this.menus = []
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_info')
    }
  }
})
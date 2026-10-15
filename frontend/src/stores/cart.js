import { defineStore } from 'pinia'
import { getCart } from '../api'

export const useCartStore = defineStore('cart', {
  state: () => ({
    count: 0
  }),
  actions: {
    async fetchCount() {
      try {
        const data = await getCart()
        this.count = data.totalCount || 0
      } catch (e) {
        this.count = 0
      }
    }
  }
})
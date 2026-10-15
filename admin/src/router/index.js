import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'product', name: 'product', component: () => import('../views/product/List.vue') },
      { path: 'category', name: 'category', component: () => import('../views/category/List.vue') },
      { path: 'order', name: 'order', component: () => import('../views/order/List.vue') },
      { path: 'user', name: 'user', component: () => import('../views/user/List.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path !== '/login' && !localStorage.getItem('admin_token')) {
    next('/login')
  } else {
    next()
  }
})

export default router
import { createRouter, createWebHistory } from 'vue-router'

const StoreLayout = () => import('../components/StoreLayout.vue')

const routes = [
  {
    path: '/',
    component: StoreLayout,
    children: [
      { path: '', name: 'home', component: () => import('../views/Home.vue') },
      { path: 'products', name: 'products', component: () => import('../views/ProductList.vue') },
      { path: 'product/:id', name: 'productDetail', component: () => import('../views/ProductDetail.vue') },
      { path: 'cart', name: 'cart', component: () => import('../views/Cart.vue'), meta: { requiresAuth: true } },
      { path: 'checkout', name: 'checkout', component: () => import('../views/Checkout.vue'), meta: { requiresAuth: true } },
      { path: 'orders', name: 'orders', component: () => import('../views/OrderList.vue'), meta: { requiresAuth: true } },
      { path: 'order/:id', name: 'orderDetail', component: () => import('../views/OrderDetail.vue'), meta: { requiresAuth: true } },
      { path: 'profile', name: 'profile', component: () => import('../views/Profile.vue'), meta: { requiresAuth: true } }
    ]
  },
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(m => m.meta.requiresAuth) && !localStorage.getItem('token')) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
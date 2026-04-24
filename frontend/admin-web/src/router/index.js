import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const routes = [
  { path: '/login',    component: () => import('../views/LoginView.vue') },
  { path: '/',         component: () => import('../views/DashboardView.vue'),      meta: { auth: true } },
  { path: '/products', component: () => import('../views/ProductManageView.vue'),  meta: { auth: true } },
  { path: '/users',    component: () => import('../views/UserManageView.vue'),     meta: { auth: true } },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.auth && !auth.isLoggedIn) return '/login'
})

export default router

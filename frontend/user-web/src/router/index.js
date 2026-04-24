import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const routes = [
  { path: '/',          component: () => import('../views/HomeView.vue') },
  { path: '/login',     component: () => import('../views/LoginView.vue') },
  { path: '/register',  component: () => import('../views/RegisterView.vue') },
  { path: '/products',  component: () => import('../views/ProductListView.vue') },
  { path: '/products/new', component: () => import('../views/ProductCreateView.vue'), meta: { auth: true } },
  { path: '/products/:id', component: () => import('../views/ProductDetailView.vue') },
  { path: '/chat',      component: () => import('../views/ChatView.vue'), meta: { auth: true } },
  { path: '/profile',   component: () => import('../views/ProfileView.vue'), meta: { auth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.auth && !auth.isLoggedIn) return '/login'
})

export default router

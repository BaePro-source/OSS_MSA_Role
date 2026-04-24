<template>
  <div class="app-wrapper">
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="logo">
          <span class="logo-icon">🛍️</span>
          <span class="logo-text">중고마켓</span>
        </router-link>

        <nav class="nav-links">
          <router-link to="/products" class="nav-link">둘러보기</router-link>
          <template v-if="auth.isLoggedIn">
            <router-link to="/products/new" class="nav-link">판매하기</router-link>
            <router-link to="/chat" class="nav-link">
              채팅
            </router-link>
          </template>
        </nav>

        <div class="nav-actions">
          <template v-if="auth.isLoggedIn">
            <router-link to="/profile" class="avatar-btn" :title="auth.user?.nickname">
              {{ auth.user?.nickname?.charAt(0)?.toUpperCase() }}
            </router-link>
            <button class="btn btn-ghost btn-sm" @click="auth.logout()">로그아웃</button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-ghost btn-sm">로그인</router-link>
            <router-link to="/register" class="btn btn-primary btn-sm">회원가입</router-link>
          </template>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from './stores/auth.js'
const auth = useAuthStore()
</script>

<style scoped>
.app-wrapper { min-height: 100vh; display: flex; flex-direction: column; }

.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border);
}
.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 800;
  color: var(--primary);
  flex-shrink: 0;
}
.logo-icon { font-size: 22px; }

.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}
.nav-link {
  padding: 6px 14px;
  border-radius: var(--r-full);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-muted);
  transition: all .15s;
}
.nav-link:hover, .nav-link.router-link-active {
  color: var(--primary);
  background: var(--primary-light);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.avatar-btn {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--primary);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 32px 24px;
}
</style>

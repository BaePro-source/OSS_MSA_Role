<template>
  <div class="admin-layout" v-if="auth.isLoggedIn">
    <!-- 사이드바 -->
    <aside class="sidebar">
      <div class="sidebar-logo">
        <span class="logo-icon">⚙️</span>
        <span class="logo-text">Admin</span>
      </div>

      <nav class="sidebar-nav">
        <router-link to="/" class="nav-item" exact-active-class="active">
          <span class="nav-icon">📊</span>
          <span>대시보드</span>
        </router-link>
        <router-link to="/products" class="nav-item" active-class="active">
          <span class="nav-icon">📦</span>
          <span>상품 관리</span>
        </router-link>
        <router-link to="/users" class="nav-item" active-class="active">
          <span class="nav-icon">👥</span>
          <span>사용자 관리</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="admin-info">
          <div class="admin-avatar">A</div>
          <div>
            <p class="admin-name">관리자</p>
            <p class="admin-role">ADMIN</p>
          </div>
        </div>
        <button class="logout-btn" @click="logout">로그아웃</button>
      </div>
    </aside>

    <!-- 메인 콘텐츠 -->
    <main class="main">
      <router-view />
    </main>
  </div>

  <!-- 로그인 화면 -->
  <router-view v-else />
</template>

<script setup>
import { useAuthStore } from './stores/auth.js'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

async function logout() {
  await auth.logout()
  router.push('/login')
}
</script>

<style>
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Pretendard', 'Noto Sans KR', sans-serif;
  background: #f1f5f9;
  color: #1e293b;
  -webkit-font-smoothing: antialiased;
}
a { text-decoration: none; color: inherit; }
button { cursor: pointer; font-family: inherit; }

/* 공용 버튼 */
.btn {
  display: inline-flex; align-items: center; justify-content: center; gap: 6px;
  padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 600;
  border: none; cursor: pointer; transition: all .15s;
}
.btn-primary { background: #84CC16; color: #fff; }
.btn-primary:hover { background: #65A30D; }
.btn-danger { background: #FEE2E2; color: #DC2626; }
.btn-danger:hover { background: #FECACA; }
.btn-ghost { background: #f1f5f9; color: #64748b; }
.btn-ghost:hover { background: #e2e8f0; }
.btn-sm { padding: 5px 12px; font-size: 12px; }

/* 공용 카드 */
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.07);
}
</style>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

/* 사이드바 */
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: #1e293b;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0; left: 0; bottom: 0;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
.logo-icon { font-size: 22px; }
.logo-text { font-size: 18px; font-weight: 800; color: #fff; }

.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255,255,255,0.6);
  transition: all .15s;
}
.nav-item:hover { background: rgba(255,255,255,0.08); color: #fff; }
.nav-item.active { background: #84CC16; color: #fff; }
.nav-icon { font-size: 16px; }

.sidebar-footer {
  padding: 16px 12px;
  border-top: 1px solid rgba(255,255,255,0.08);
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.admin-info { display: flex; align-items: center; gap: 10px; }
.admin-avatar {
  width: 34px; height: 34px; border-radius: 50%;
  background: #84CC16; color: #fff;
  font-size: 14px; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}
.admin-name { font-size: 13px; font-weight: 600; color: #fff; }
.admin-role { font-size: 11px; color: rgba(255,255,255,0.4); }
.logout-btn {
  width: 100%; padding: 8px; border-radius: 8px; border: none;
  background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.6);
  font-size: 13px; font-weight: 500; cursor: pointer; transition: .15s;
}
.logout-btn:hover { background: rgba(255,255,255,0.14); color: #fff; }

/* 메인 */
.main {
  margin-left: 220px;
  flex: 1;
  padding: 32px;
  min-height: 100vh;
}
</style>

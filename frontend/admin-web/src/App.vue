<template>
  <div>
    <nav v-if="auth.isLoggedIn" style="padding:12px 24px;background:#343a40;color:white;display:flex;gap:16px">
      <router-link to="/" style="color:white">대시보드</router-link>
      <router-link to="/products" style="color:white">게시글 관리</router-link>
      <router-link to="/users" style="color:white">사용자 관리</router-link>
      <button @click="auth.logout()" style="margin-left:auto">로그아웃</button>
    </nav>
    <main style="padding:24px">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from './stores/auth.js'
const auth = useAuthStore()
</script>

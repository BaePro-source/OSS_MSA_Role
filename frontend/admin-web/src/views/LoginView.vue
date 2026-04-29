<template>
  <div class="login-wrap">
    <div class="login-card">
      <div class="login-header">
        <span class="login-logo">⚙️</span>
        <h1 class="login-title">관리자 로그인</h1>
        <p class="login-sub">관리자 계정으로 로그인하세요</p>
      </div>

      <form @submit.prevent="submit" class="login-form">
        <div class="field">
          <label class="field-label">이메일</label>
          <input v-model="email" type="email" class="input" placeholder="admin@example.com" required />
        </div>
        <div class="field">
          <label class="field-label">비밀번호</label>
          <input v-model="password" type="password" class="input" placeholder="••••••••" required />
        </div>
        <p v-if="error" class="error-msg">{{ error }}</p>
        <button type="submit" class="btn btn-primary login-btn" :disabled="loading">
          {{ loading ? '로그인 중...' : '로그인' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
const router = useRouter()
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  loading.value = true
  error.value = ''
  try {
    await auth.login(email.value, password.value)
    router.push('/')
  } catch (e) {
    error.value = e.message || '이메일 또는 비밀번호가 올바르지 않습니다.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
}
.login-card {
  width: 400px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
}
.login-header { text-align: center; margin-bottom: 32px; }
.login-logo { font-size: 40px; display: block; margin-bottom: 12px; }
.login-title { font-size: 22px; font-weight: 800; margin-bottom: 6px; }
.login-sub { font-size: 14px; color: #64748b; }

.login-form { display: flex; flex-direction: column; gap: 16px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 13px; font-weight: 600; color: #374151; }
.input {
  padding: 11px 14px; border: 1.5px solid #e2e8f0;
  border-radius: 8px; font-size: 14px; font-family: inherit; outline: none;
  transition: border-color .15s;
}
.input:focus { border-color: #84CC16; }
.error-msg {
  font-size: 13px; color: #DC2626;
  background: #FEE2E2; border-radius: 6px; padding: 10px 14px;
}
.login-btn { width: 100%; padding: 13px; font-size: 15px; margin-top: 4px; }
</style>

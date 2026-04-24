<template>
  <div class="auth-wrap">
    <div class="auth-card">
      <div class="auth-logo">
        <span class="auth-logo-icon">🛍️</span>
        <span class="auth-logo-text">중고마켓</span>
      </div>
      <h2 class="auth-title">시작해볼까요?</h2>
      <p class="auth-sub">계정을 만들고 AI 추천 서비스를 무료로 이용하세요</p>

      <form @submit.prevent="submit" class="auth-form">
        <div class="field">
          <label class="field-label">이메일</label>
          <input v-model="form.email" type="email" class="input" placeholder="hello@example.com" required />
        </div>
        <div class="field">
          <label class="field-label">비밀번호</label>
          <input v-model="form.password" type="password" class="input" placeholder="6자 이상 입력" required />
        </div>
        <div class="field">
          <label class="field-label">닉네임</label>
          <input v-model="form.nickname" class="input" placeholder="사용할 닉네임" required />
        </div>

        <p v-if="error" class="form-error">{{ error }}</p>

        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '가입 중...' : '무료로 시작하기' }}
        </button>
      </form>

      <p class="auth-footer">
        이미 계정이 있으신가요?
        <router-link to="/login" class="auth-link">로그인</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
const router = useRouter()
const form = ref({ email: '', password: '', nickname: '', latitude: 0, longitude: 0 })
const error = ref('')
const loading = ref(false)

async function submit() {
  loading.value = true
  error.value = ''
  try {
    await auth.register(form.value)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.message || '회원가입에 실패했어요. 다시 시도해 주세요.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrap {
  min-height: calc(100vh - 160px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}
.auth-card {
  background: var(--surface);
  border-radius: var(--r-xl);
  box-shadow: var(--shadow-lg);
  padding: 48px 40px;
  width: 100%;
  max-width: 420px;
}
.auth-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 800;
  color: var(--primary);
  margin-bottom: 28px;
}
.auth-logo-icon { font-size: 26px; }
.auth-title {
  font-size: 22px;
  font-weight: 800;
  text-align: center;
  margin-bottom: 8px;
}
.auth-sub {
  font-size: 14px;
  color: var(--text-muted);
  text-align: center;
  margin-bottom: 32px;
}
.auth-form { display: flex; flex-direction: column; gap: 16px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 13px; font-weight: 600; color: var(--text); }
.form-error {
  font-size: 13px;
  color: var(--danger);
  background: #FFF5F5;
  border: 1px solid #FFCDD2;
  border-radius: var(--r-sm);
  padding: 10px 14px;
  margin: 0;
}
.btn-block { width: 100%; margin-top: 4px; }
.btn-block:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }
.auth-footer {
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
  margin-top: 24px;
}
.auth-link {
  color: var(--primary);
  font-weight: 600;
  margin-left: 4px;
}
.auth-link:hover { text-decoration: underline; }
</style>

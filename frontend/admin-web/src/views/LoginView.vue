<template>
  <div style="max-width:360px;margin:80px auto">
    <h2>관리자 로그인</h2>
    <form @submit.prevent="submit">
      <div><input v-model="email" type="email" placeholder="이메일" required /></div>
      <div><input v-model="password" type="password" placeholder="비밀번호" required /></div>
      <p v-if="error" style="color:red">{{ error }}</p>
      <button type="submit">로그인</button>
    </form>
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

async function submit() {
  try {
    await auth.login(email.value, password.value)
    router.push('/')
  } catch (e) {
    error.value = e.message || '로그인 실패'
  }
}
</script>

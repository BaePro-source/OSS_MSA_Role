<template>
  <div>
    <h2>낮은 평점 사용자 관리</h2>
    <p>평균 평점 2.0 이하 사용자 목록</p>
    <table style="width:100%;border-collapse:collapse">
      <thead>
        <tr style="background:#f8f9fa">
          <th style="padding:8px;border:1px solid #ddd">사용자 ID</th>
          <th style="padding:8px;border:1px solid #ddd">평균 평점</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="u in users" :key="u[0]">
          <td style="padding:8px;border:1px solid #ddd">{{ u[0] }}</td>
          <td style="padding:8px;border:1px solid #ddd">{{ Number(u[1]).toFixed(2) }}</td>
        </tr>
        <tr v-if="!users.length">
          <td colspan="2" style="padding:8px;text-align:center">낮은 평점 사용자 없음</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/index.js'

const users = ref([])

onMounted(async () => {
  const { data } = await api.get('/admin/users/low-rated?threshold=2.0')
  users.value = data
})
</script>

<template>
  <div>
    <div class="page-header">
      <div>
        <h1 class="page-title">사용자 관리</h1>
        <p class="page-sub">전체 {{ users.length }}명</p>
      </div>
      <div class="header-actions">
        <input v-model="keyword" class="search-input" placeholder="🔍 닉네임/이메일 검색..." />
        <button
          class="btn btn-sm"
          :class="showLowOnly ? 'btn-primary' : 'btn-ghost'"
          @click="showLowOnly = !showLowOnly"
        >
          ⚠️ 낮은 평점만
        </button>
      </div>
    </div>

    <div class="card">
      <div v-if="loading" class="empty">불러오는 중...</div>
      <table class="table" v-else-if="filtered.length">
        <thead>
          <tr>
            <th>ID</th>
            <th>닉네임</th>
            <th>이메일</th>
            <th>역할</th>
            <th>평균 평점</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in filtered" :key="u.id" :class="{ banned: u.banned }">
            <td class="muted">#{{ u.id }}</td>
            <td class="bold">{{ u.nickname }}</td>
            <td class="muted">{{ u.email }}</td>
            <td>
              <span class="badge" :class="u.role === 'ADMIN' ? 'badge-admin' : 'badge-user'">
                {{ u.role === 'ADMIN' ? '관리자' : '일반' }}
              </span>
            </td>
            <td>
              <span v-if="lowRatedMap[u.id]" class="rating-bad">
                ★ {{ Number(lowRatedMap[u.id]).toFixed(2) }}
              </span>
              <span v-else class="muted">-</span>
            </td>
            <td>
              <span class="badge" :class="u.banned ? 'badge-banned' : 'badge-active'">
                {{ u.banned ? '정지됨' : '정상' }}
              </span>
            </td>
            <td>
              <button
                class="btn btn-sm"
                :class="u.banned ? 'btn-ghost' : 'btn-danger'"
                @click="toggleBan(u)"
              >
                {{ u.banned ? '정지 해제' : '정지' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty">검색 결과가 없습니다</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api/index.js'

const users = ref([])
const lowRatedMap = ref({})
const keyword = ref('')
const showLowOnly = ref(false)
const loading = ref(true)

const filtered = computed(() => {
  let list = users.value
  if (showLowOnly.value) list = list.filter(u => lowRatedMap.value[u.id])
  if (keyword.value.trim()) {
    const kw = keyword.value.toLowerCase()
    list = list.filter(u =>
      u.nickname?.toLowerCase().includes(kw) || u.email?.toLowerCase().includes(kw)
    )
  }
  return list
})

onMounted(async () => {
  try {
    const [usersRes, lowRes] = await Promise.allSettled([
      api.get('/admin/users'),
      api.get('/admin/users/low-rated'),
    ])
    if (usersRes.status === 'fulfilled') users.value = usersRes.value.data
    if (lowRes.status === 'fulfilled') {
      lowRes.value.data.forEach(([id, avg]) => {
        lowRatedMap.value[id] = avg
      })
    }
  } finally {
    loading.value = false
  }
})

async function toggleBan(user) {
  const action = user.banned ? '정지를 해제' : '정지'
  if (!confirm(`${user.nickname}님을 ${action}하시겠습니까?`)) return
  await api.patch(`/admin/users/${user.id}/ban`)
  user.banned = !user.banned
}
</script>

<style scoped>
.page-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  margin-bottom: 24px;
}
.page-title { font-size: 24px; font-weight: 800; margin-bottom: 4px; }
.page-sub { font-size: 14px; color: #64748b; }
.header-actions { display: flex; gap: 8px; align-items: center; }

.search-input {
  padding: 9px 14px; border: 1.5px solid #e2e8f0; border-radius: 8px;
  font-size: 14px; font-family: inherit; outline: none; width: 260px;
  transition: border-color .15s;
}
.search-input:focus { border-color: #84CC16; }

.card { background: #fff; border-radius: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); overflow: hidden; }

.table { width: 100%; border-collapse: collapse; }
.table th {
  text-align: left; font-size: 12px; font-weight: 600; color: #64748b;
  padding: 12px 16px; border-bottom: 1px solid #e2e8f0;
  text-transform: uppercase; letter-spacing: 0.5px; background: #f8fafc;
}
.table td { padding: 13px 16px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
.table tr:last-child td { border-bottom: none; }
.table tr:hover td { background: #f8fafc; }
.table tr.banned td { opacity: 0.55; }
.muted { color: #94a3b8; }
.bold { font-weight: 600; }

.badge {
  display: inline-flex; padding: 3px 10px;
  border-radius: 9999px; font-size: 12px; font-weight: 600;
}
.badge-admin  { background: #EDE9FE; color: #7C3AED; }
.badge-user   { background: #F1F5F9; color: #475569; }
.badge-active { background: #DCFCE7; color: #16A34A; }
.badge-banned { background: #FEE2E2; color: #DC2626; }

.rating-bad { color: #DC2626; font-weight: 700; font-size: 13px; }
.empty { text-align: center; padding: 48px; color: #94a3b8; font-size: 14px; }
</style>

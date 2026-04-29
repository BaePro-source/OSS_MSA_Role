<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">대시보드</h1>
      <p class="page-sub">서비스 현황을 한눈에 확인하세요</p>
    </div>

    <!-- 통계 카드 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="s in statCards" :key="s.label">
        <div class="stat-icon" :style="{ background: s.bg }">{{ s.icon }}</div>
        <div class="stat-body">
          <p class="stat-num">{{ s.value }}</p>
          <p class="stat-label">{{ s.label }}</p>
        </div>
      </div>
    </div>

    <!-- 최근 상품 -->
    <div class="card section-card">
      <div class="section-head">
        <h2 class="section-title">최근 등록 상품</h2>
        <router-link to="/products" class="btn btn-ghost btn-sm">전체 보기 →</router-link>
      </div>
      <table class="table" v-if="recentProducts.length">
        <thead>
          <tr>
            <th>ID</th><th>제목</th><th>가격</th><th>상태</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in recentProducts" :key="p.id">
            <td class="muted">#{{ p.id }}</td>
            <td class="bold">{{ p.title }}</td>
            <td>{{ p.price?.toLocaleString() }}원</td>
            <td><span class="badge" :class="statusClass(p.status)">{{ statusText(p.status) }}</span></td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">등록된 상품이 없습니다</p>
    </div>

    <!-- 낮은 평점 사용자 -->
    <div class="card section-card">
      <div class="section-head">
        <h2 class="section-title">⚠️ 낮은 평점 사용자 <span class="warn-badge">평점 2.0 이하</span></h2>
        <router-link to="/users" class="btn btn-ghost btn-sm">사용자 관리 →</router-link>
      </div>
      <table class="table" v-if="lowRatedUsers.length">
        <thead>
          <tr><th>사용자 ID</th><th>평균 평점</th></tr>
        </thead>
        <tbody>
          <tr v-for="u in lowRatedUsers" :key="u[0]">
            <td class="muted">#{{ u[0] }}</td>
            <td><span class="rating-bad">★ {{ Number(u[1]).toFixed(2) }}</span></td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">낮은 평점 사용자가 없습니다 👍</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api/index.js'

const stats = ref({ totalProducts: 0, totalUsers: 0 })
const recentProducts = ref([])
const lowRatedUsers = ref([])

const statCards = computed(() => [
  { icon: '📦', label: '전체 상품', value: stats.value.totalProducts, bg: '#EFF6FF' },
  { icon: '👥', label: '전체 사용자', value: stats.value.totalUsers, bg: '#F0FDF4' },
  { icon: '🛒', label: '판매 완료', value: recentProducts.value.filter(p => p.status === 'SOLD').length, bg: '#FFF7ED' },
  { icon: '📋', label: '판매 중', value: recentProducts.value.filter(p => p.status === 'ON_SALE').length, bg: '#FDF4FF' },
])

function statusText(s) { return { ON_SALE: '판매중', RESERVED: '예약중', SOLD: '거래완료' }[s] || s }
function statusClass(s) { return { ON_SALE: 'badge-sale', RESERVED: 'badge-reserve', SOLD: 'badge-sold' }[s] || '' }

onMounted(async () => {
  const [statsRes, productsRes, lowRatedRes] = await Promise.allSettled([
    api.get('/admin/stats'),
    api.get('/admin/products'),
    api.get('/admin/users/low-rated'),
  ])
  if (statsRes.status === 'fulfilled') stats.value = statsRes.value.data
  if (productsRes.status === 'fulfilled') recentProducts.value = productsRes.value.data.slice(0, 5)
  if (lowRatedRes.status === 'fulfilled') lowRatedUsers.value = lowRatedRes.value.data
})
</script>

<style scoped>
.page-header { margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 800; margin-bottom: 4px; }
.page-sub { font-size: 14px; color: #64748b; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px; flex-shrink: 0;
}
.stat-num { font-size: 26px; font-weight: 800; color: #1e293b; margin-bottom: 2px; }
.stat-label { font-size: 12px; color: #64748b; font-weight: 500; }

.section-card { padding: 24px; margin-bottom: 20px; }
.section-head {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 16px;
}
.section-title { font-size: 16px; font-weight: 700; display: flex; align-items: center; gap: 8px; }
.warn-badge {
  font-size: 11px; font-weight: 600; padding: 2px 8px;
  background: #FEF3C7; color: #D97706; border-radius: 9999px;
}

.table { width: 100%; border-collapse: collapse; }
.table th {
  text-align: left; font-size: 12px; font-weight: 600;
  color: #64748b; padding: 8px 12px;
  border-bottom: 1px solid #e2e8f0;
  text-transform: uppercase; letter-spacing: 0.5px;
}
.table td { padding: 12px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
.table tr:last-child td { border-bottom: none; }
.muted { color: #94a3b8; }
.bold { font-weight: 600; }

.badge {
  display: inline-flex; padding: 3px 10px;
  border-radius: 9999px; font-size: 12px; font-weight: 600;
}
.badge-sale    { background: #DCFCE7; color: #16A34A; }
.badge-reserve { background: #FEF9C3; color: #CA8A04; }
.badge-sold    { background: #F1F5F9; color: #94A3B8; }

.rating-bad { color: #DC2626; font-weight: 700; font-size: 13px; }
.empty { text-align: center; padding: 32px; color: #94a3b8; font-size: 14px; }
</style>

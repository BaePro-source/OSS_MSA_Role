<template>
  <div>
    <div class="page-header">
      <div>
        <h1 class="page-title">상품 관리</h1>
        <p class="page-sub">전체 {{ products.length }}개 상품</p>
      </div>
      <div class="header-actions">
        <input v-model="keyword" class="search-input" placeholder="🔍 제목 검색..." />
      </div>
    </div>

    <div class="card">
      <div v-if="loading" class="empty">불러오는 중...</div>
      <table class="table" v-else-if="filtered.length">
        <thead>
          <tr>
            <th>ID</th>
            <th>제목</th>
            <th>가격</th>
            <th>카테고리</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in filtered" :key="p.id">
            <td class="muted">#{{ p.id }}</td>
            <td class="bold">{{ p.title }}</td>
            <td>{{ p.price?.toLocaleString() }}원</td>
            <td>{{ p.category }}</td>
            <td><span class="badge" :class="statusClass(p.status)">{{ statusText(p.status) }}</span></td>
            <td>
              <button class="btn btn-danger btn-sm" @click="deleteProduct(p.id)">삭제</button>
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

const products = ref([])
const keyword = ref('')
const loading = ref(true)

const filtered = computed(() => {
  if (!keyword.value.trim()) return products.value
  return products.value.filter(p =>
    p.title?.toLowerCase().includes(keyword.value.toLowerCase())
  )
})

function statusText(s) { return { ON_SALE: '판매중', RESERVED: '예약중', SOLD: '거래완료' }[s] || s }
function statusClass(s) { return { ON_SALE: 'badge-sale', RESERVED: 'badge-reserve', SOLD: 'badge-sold' }[s] || '' }

onMounted(async () => {
  try {
    const { data } = await api.get('/admin/products')
    products.value = data
  } finally {
    loading.value = false
  }
})

async function deleteProduct(id) {
  if (!confirm('이 상품을 삭제하시겠습니까?')) return
  await api.delete(`/admin/products/${id}`)
  products.value = products.value.filter(p => p.id !== id)
}
</script>

<style scoped>
.page-header {
  display: flex; align-items: flex-start; justify-content: space-between;
  margin-bottom: 24px;
}
.page-title { font-size: 24px; font-weight: 800; margin-bottom: 4px; }
.page-sub { font-size: 14px; color: #64748b; }

.search-input {
  padding: 9px 14px; border: 1.5px solid #e2e8f0; border-radius: 8px;
  font-size: 14px; font-family: inherit; outline: none; width: 240px;
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
.muted { color: #94a3b8; }
.bold { font-weight: 600; }

.badge {
  display: inline-flex; padding: 3px 10px;
  border-radius: 9999px; font-size: 12px; font-weight: 600;
}
.badge-sale    { background: #DCFCE7; color: #16A34A; }
.badge-reserve { background: #FEF9C3; color: #CA8A04; }
.badge-sold    { background: #F1F5F9; color: #94A3B8; }

.empty { text-align: center; padding: 48px; color: #94a3b8; font-size: 14px; }
</style>

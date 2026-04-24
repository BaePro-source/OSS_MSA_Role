<template>
  <div class="profile-wrap">
    <!-- 프로필 헤더 -->
    <div class="profile-header">
      <div class="profile-avatar">
        {{ auth.user?.nickname?.charAt(0)?.toUpperCase() }}
      </div>
      <div class="profile-info">
        <h1 class="profile-name">{{ auth.user?.nickname }}</h1>
        <span class="profile-role" :class="auth.user?.role === 'ADMIN' ? 'role-admin' : 'role-user'">
          {{ auth.user?.role === 'ADMIN' ? '관리자' : '일반 회원' }}
        </span>
      </div>
    </div>

    <!-- 통계 카드 -->
    <div class="stats-row">
      <div class="stat-card">
        <p class="stat-num">{{ myProducts.length }}</p>
        <p class="stat-label">판매 물품</p>
      </div>
      <div class="stat-card">
        <p class="stat-num">{{ myReviews.length }}</p>
        <p class="stat-label">받은 리뷰</p>
      </div>
      <div class="stat-card">
        <p class="stat-num">{{ avgRating }}</p>
        <p class="stat-label">평균 평점</p>
      </div>
    </div>

    <!-- 내 판매 목록 -->
    <div class="profile-section">
      <div class="section-head">
        <h2 class="section-title">내 판매 목록</h2>
        <router-link to="/products/new" class="btn btn-primary btn-sm">+ 새 등록</router-link>
      </div>

      <div v-if="myProducts.length" class="my-products">
        <router-link
          v-for="p in myProducts" :key="p.id"
          :to="`/products/${p.id}`"
          class="my-product-item"
        >
          <div class="my-product-info">
            <p class="my-product-title">{{ p.title }}</p>
            <p class="my-product-price">{{ p.price?.toLocaleString() }}원</p>
          </div>
          <span class="product-status" :class="statusClass(p.status)">{{ statusText(p.status) }}</span>
        </router-link>
      </div>
      <p v-else class="section-empty">아직 등록한 물품이 없어요</p>
    </div>

    <!-- 받은 리뷰 -->
    <div class="profile-section">
      <h2 class="section-title">받은 리뷰</h2>
      <div v-if="myReviews.length" class="reviews-list">
        <div v-for="r in myReviews" :key="r.id" class="review-item">
          <div class="review-stars">
            <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= r.rating }">★</span>
          </div>
          <p class="review-content">{{ r.content }}</p>
        </div>
      </div>
      <p v-else class="section-empty">아직 받은 리뷰가 없어요</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api/index.js'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
const myProducts = ref([])
const myReviews = ref([])

const avgRating = computed(() => {
  if (!myReviews.value.length) return '-'
  const sum = myReviews.value.reduce((acc, r) => acc + r.rating, 0)
  return (sum / myReviews.value.length).toFixed(1)
})

function statusText(s) { return { ON_SALE: '판매중', RESERVED: '예약중', SOLD: '거래완료' }[s] || s }
function statusClass(s) { return { ON_SALE: 'badge-sale', RESERVED: 'badge-reserve', SOLD: 'badge-sold' }[s] || '' }

onMounted(async () => {
  const [prod, rev] = await Promise.all([
    api.get(`/products/seller/${auth.user?.userId}`),
    api.get(`/reviews/user/${auth.user?.userId}`)
  ])
  myProducts.value = prod.data
  myReviews.value = rev.data
})
</script>

<style scoped>
.profile-wrap { max-width: 680px; margin: 0 auto; }

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  background: var(--surface);
  border-radius: var(--r-xl);
  padding: 28px 32px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 20px;
}
.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: var(--primary);
  color: #fff;
  font-size: 28px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(255,107,53,.35);
}
.profile-name {
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 8px;
}
.profile-role {
  display: inline-flex;
  padding: 3px 12px;
  border-radius: var(--r-full);
  font-size: 12px;
  font-weight: 700;
}
.role-user  { background: var(--primary-light); color: var(--primary); }
.role-admin { background: #E3F2FD; color: #1565C0; }

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}
.stat-card {
  background: var(--surface);
  border-radius: var(--r-lg);
  padding: 20px;
  text-align: center;
  box-shadow: var(--shadow-sm);
}
.stat-num { font-size: 26px; font-weight: 800; color: var(--primary); margin-bottom: 4px; }
.stat-label { font-size: 12px; color: var(--text-muted); font-weight: 600; }

.profile-section {
  background: var(--surface);
  border-radius: var(--r-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 16px;
}
.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.section-title { font-size: 16px; font-weight: 800; }
.section-empty { font-size: 13px; color: var(--text-muted); text-align: center; padding: 24px 0; }

.my-products { display: flex; flex-direction: column; gap: 10px; }
.my-product-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: var(--bg);
  border-radius: var(--r-md);
  transition: background .15s;
}
.my-product-item:hover { background: var(--primary-light); }
.my-product-title { font-size: 14px; font-weight: 600; margin-bottom: 2px; }
.my-product-price { font-size: 13px; color: var(--primary); font-weight: 700; }
.product-status {
  flex-shrink: 0;
  padding: 4px 12px;
  border-radius: var(--r-full);
  font-size: 12px;
  font-weight: 700;
}
.badge-sale    { background: #E8F5E9; color: #2E7D32; }
.badge-reserve { background: #FFF8E1; color: #E65100; }
.badge-sold    { background: #F5F5F5; color: #9E9E9E; }

.reviews-list { display: flex; flex-direction: column; gap: 12px; }
.review-item {
  padding: 14px 16px;
  background: var(--bg);
  border-radius: var(--r-md);
}
.review-stars { display: flex; gap: 2px; margin-bottom: 6px; }
.star { font-size: 16px; color: #ddd; }
.star.filled { color: #FFB300; }
.review-content { font-size: 14px; color: var(--text); line-height: 1.5; }
</style>

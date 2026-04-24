<template>
  <div v-if="product" class="detail-wrap">
    <!-- 이미지 -->
    <div class="detail-img-wrap">
      <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.title" class="detail-img" />
      <div v-else class="detail-img-placeholder">📦</div>
      <span class="detail-badge" :class="badgeClass">{{ badgeText }}</span>
    </div>

    <!-- 본문 -->
    <div class="detail-body">
      <div class="detail-header">
        <div>
          <p class="detail-category">{{ product.category }}</p>
          <h1 class="detail-title">{{ product.title }}</h1>
          <p class="detail-price">{{ product.price.toLocaleString() }}원</p>
        </div>
        <button
          v-if="auth.isLoggedIn && product.sellerId !== auth.user?.userId"
          class="btn btn-primary btn-lg chat-btn"
          @click="startChat"
        >
          💬 채팅으로 거래하기
        </button>
      </div>

      <div class="divider" />

      <p class="detail-desc">{{ product.description }}</p>

      <!-- 판매자 신뢰도 -->
      <div v-if="trust" class="trust-card">
        <div class="trust-header">
          <span class="trust-icon">🛡️</span>
          <span class="trust-label">판매자 신뢰도</span>
          <span class="trust-badge" :class="`trust-${trust.trust_level?.toLowerCase()}`">
            {{ trust.trust_level }} · {{ trust.trust_score }}점
          </span>
        </div>
        <p class="trust-summary">{{ trust.summary }}</p>
        <p v-if="trust.warning" class="trust-warning">⚠️ {{ trust.warning }}</p>
      </div>
      <div v-else-if="trustLoading" class="trust-loading">
        <span>신뢰도 분석 중...</span>
      </div>
    </div>
  </div>

  <div v-else class="loading-wrap">
    <p>물품 정보를 불러오는 중...</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api/index.js'
import { useAuthStore } from '../stores/auth.js'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const product = ref(null)
const trust = ref(null)
const trustLoading = ref(true)

const badgeText = computed(() => ({
  ON_SALE: '판매중', RESERVED: '예약중', SOLD: '거래완료'
}[product.value?.status] || '판매중'))

const badgeClass = computed(() => ({
  ON_SALE: 'badge-sale', RESERVED: 'badge-reserve', SOLD: 'badge-sold'
}[product.value?.status] || 'badge-sale'))

onMounted(async () => {
  const { data } = await api.get(`/products/${route.params.id}`)
  product.value = data

  try {
    const reviews = await api.get(`/reviews/user/${data.sellerId}`)
    const summary = await api.get(`/reviews/user/${data.sellerId}/summary`)
    const { data: trustData } = await api.post('/ai/trust/evaluate', {
      target_user_id: data.sellerId,
      reviews: reviews.data,
      average_rating: summary.data.averageRating
    })
    trust.value = trustData
  } catch { /* 비필수 기능 */ } finally {
    trustLoading.value = false
  }
})

async function startChat() {
  const { data } = await api.post('/chat/rooms', {
    productId: product.value.id,
    sellerId: product.value.sellerId
  })
  router.push(`/chat?roomId=${data.id}`)
}
</script>

<style scoped>
.detail-wrap {
  max-width: 720px;
  margin: 0 auto;
}

.detail-img-wrap {
  position: relative;
  width: 100%;
  padding-top: 56%;
  background: var(--bg);
  border-radius: var(--r-xl);
  overflow: hidden;
  margin-bottom: 28px;
}
.detail-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.detail-img-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64px;
}
.detail-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 5px 14px;
  border-radius: var(--r-full);
  font-size: 13px;
  font-weight: 700;
}
.badge-sale    { background: rgba(0,200,83,0.18); color: #2E7D32; }
.badge-reserve { background: rgba(255,179,0,0.18); color: #E65100; }
.badge-sold    { background: rgba(0,0,0,0.1); color: #777; }

.detail-body { padding: 0 4px; }
.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
}
.detail-category {
  font-size: 12px;
  font-weight: 600;
  color: var(--primary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 6px;
}
.detail-title {
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 8px;
  line-height: 1.3;
}
.detail-price {
  font-size: 22px;
  font-weight: 800;
  color: var(--primary);
}
.chat-btn { flex-shrink: 0; }

.detail-desc {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text);
  margin-bottom: 28px;
  white-space: pre-wrap;
}

.trust-card {
  background: var(--bg);
  border-radius: var(--r-lg);
  padding: 20px;
  border: 1px solid var(--border);
}
.trust-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.trust-icon { font-size: 18px; }
.trust-label { font-size: 14px; font-weight: 700; flex: 1; }
.trust-badge {
  padding: 3px 12px;
  border-radius: var(--r-full);
  font-size: 12px;
  font-weight: 700;
}
.trust-high   { background: #E8F5E9; color: #2E7D32; }
.trust-medium { background: #FFF8E1; color: #E65100; }
.trust-low    { background: #FFF3E0; color: #BF360C; }
.trust-danger { background: #FFEBEE; color: #B71C1C; }

.trust-summary { font-size: 13px; color: var(--text-muted); line-height: 1.6; }
.trust-warning {
  font-size: 13px;
  color: var(--danger);
  margin-top: 8px;
  font-weight: 600;
}

.trust-loading {
  padding: 16px;
  background: var(--bg);
  border-radius: var(--r-lg);
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
}

.loading-wrap {
  text-align: center;
  padding: 80px 24px;
  color: var(--text-muted);
}
</style>

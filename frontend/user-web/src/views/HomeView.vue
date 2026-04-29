<template>
  <div>
    <!-- Hero -->
    <section class="hero">
      <!-- 배경 장식 -->
      <div class="hero-bg">
        <div class="hero-blob blob-1" />
        <div class="hero-blob blob-2" />
        <div class="hero-blob blob-3" />
      </div>

      <div class="hero-inner">
        <!-- 왼쪽: 텍스트 -->
        <div class="hero-content">
          <p class="hero-badge">🔥 지금 주변에서 거래 중</p>
          <h1 class="hero-title">내 주변 중고거래,<br>이제 더 스마트하게</h1>
          <p class="hero-sub">AI가 당신에게 맞는 물품을 추천해 드려요.<br>믿을 수 있는 판매자와 안전하게 거래하세요.</p>
          <div class="hero-actions">
            <router-link to="/products" class="btn hero-btn-primary btn-lg">지금 둘러보기</router-link>
            <router-link v-if="auth.isLoggedIn" to="/products/new" class="btn hero-btn-ghost btn-lg">물품 등록하기</router-link>
            <router-link v-else to="/register" class="btn hero-btn-ghost btn-lg">무료로 시작하기</router-link>
          </div>

          <!-- 통계 -->
          <div class="hero-stats">
            <div class="hero-stat">
              <p class="hero-stat-num">{{ stats.products }}+</p>
              <p class="hero-stat-label">등록 물품</p>
            </div>
            <div class="hero-stat-divider" />
            <div class="hero-stat">
              <p class="hero-stat-num">{{ stats.sold }}+</p>
              <p class="hero-stat-label">완료 거래</p>
            </div>
            <div class="hero-stat-divider" />
            <div class="hero-stat">
              <p class="hero-stat-num">AI</p>
              <p class="hero-stat-label">스마트 추천</p>
            </div>
          </div>
        </div>

        <!-- 오른쪽: 카드 쇼케이스 -->
        <div class="hero-visual">
          <!-- 메인 카드 -->
          <router-link
            v-if="heroProducts[0]"
            :to="`/products/${heroProducts[0].id}`"
            class="hero-main-card"
          >
            <div class="hmc-img">
              <img v-if="heroProducts[0].imageUrl" :src="heroProducts[0].imageUrl" :alt="heroProducts[0].title" />
              <span v-else class="hmc-emoji">{{ categoryEmoji(heroProducts[0].category) }}</span>
            </div>
            <div class="hmc-body">
              <span class="hmc-category">{{ heroProducts[0].category }}</span>
              <p class="hmc-title">{{ heroProducts[0].title }}</p>
              <p class="hmc-price">{{ heroProducts[0].price.toLocaleString() }}원</p>
            </div>
            <span class="hmc-badge">NEW</span>
          </router-link>
          <div v-else class="hero-main-card placeholder-main">
            <div class="hmc-img"><span class="hmc-emoji">📱</span></div>
            <div class="hmc-body">
              <span class="hmc-category">전자기기</span>
              <p class="hmc-title">지금 첫 물품을 등록해보세요</p>
              <p class="hmc-price">무료 등록</p>
            </div>
          </div>

          <!-- 사이드 카드들 -->
          <div class="hero-side-cards">
            <router-link
              v-for="(p, i) in heroProducts.slice(1, 3)" :key="p.id"
              :to="`/products/${p.id}`"
              class="hero-side-card"
            >
              <span class="hsc-emoji">{{ categoryEmoji(p.category) }}</span>
              <div class="hsc-info">
                <p class="hsc-title">{{ p.title }}</p>
                <p class="hsc-price">{{ p.price.toLocaleString() }}원</p>
              </div>
            </router-link>

            <!-- 플레이스홀더 채우기 -->
            <div v-for="i in Math.max(0, 2 - heroProducts.slice(1,3).length)" :key="'ph'+i" class="hero-side-card placeholder-side">
              <span class="hsc-emoji">{{ ['👕','🪑'][i] }}</span>
              <div class="hsc-info">
                <p class="hsc-title">AI 추천 물품</p>
                <p class="hsc-price">둘러보기 →</p>
              </div>
            </div>

            <!-- AI 추천 배지 -->
            <div class="hero-ai-badge">
              <span>🤖</span>
              <p>AI가 지금 추천 중</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="features">
      <h2 class="section-title">왜 중고마켓인가요?</h2>
      <div class="features-grid">
        <div class="feature-card" v-for="f in features" :key="f.icon">
          <span class="feature-icon">{{ f.icon }}</span>
          <h3>{{ f.title }}</h3>
          <p>{{ f.desc }}</p>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-banner">
      <template v-if="auth.isLoggedIn">
        <h2>오늘 뭔가 팔고 싶으신가요?</h2>
        <p>지금 바로 물품을 등록하고 거래를 시작해 보세요</p>
        <router-link to="/products/new" class="btn btn-primary btn-lg">물품 등록하기</router-link>
      </template>
      <template v-else>
        <h2>지금 바로 시작해보세요</h2>
        <p>가입하면 AI 추천 서비스를 무료로 이용할 수 있어요</p>
        <router-link to="/register" class="btn btn-primary btn-lg">무료 회원가입</router-link>
      </template>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth.js'
import api from '../api/index.js'

const auth = useAuthStore()
const heroProducts = ref([])
const stats = ref({ products: 0, sold: 0 })

const CATEGORY_EMOJI = {
  '전자기기': '📱', '의류': '👕', '가구': '🪑',
  '도서': '📚', '스포츠': '⚽', '기타': '📦'
}
function categoryEmoji(cat) { return CATEGORY_EMOJI[cat] || '📦' }

onMounted(async () => {
  try {
    const { data } = await api.get('/products')
    heroProducts.value = data.filter(p => p.status === 'ON_SALE').slice(0, 3)
    stats.value.products = data.length
    stats.value.sold = data.filter(p => p.status === 'SOLD').length
  } catch {}
})

const features = [
  { icon: '📍', title: '위치 기반 추천', desc: '내 주변 5km 이내 물품을 먼저 보여줘요. 직거래가 훨씬 쉬워져요.' },
  { icon: '🤖', title: 'AI 스마트 추천', desc: '채팅으로 원하는 물품을 말하면 AI가 딱 맞는 것을 찾아줘요.' },
  { icon: '🛡️', title: '신뢰도 검증', desc: '판매자 리뷰를 AI가 분석해서 안전한 거래 상대를 알려줘요.' },
  { icon: '💬', title: '실시간 채팅', desc: '관심 물품이 생기면 바로 채팅으로 가격 흥정을 해보세요.' },
]
</script>

<style scoped>
/* ── Hero (full-width breakout) ── */
.hero {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0f1a0a 0%, #1a2e10 55%, #0d1f1a 100%);
  width: 100vw;
  margin-left: calc(-50vw + 50%);
  margin-top: -32px;
  margin-bottom: 60px;
}

.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}
.hero-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  opacity: 0.4;
}
.blob-1 {
  width: 520px; height: 520px;
  background: #84CC16;
  top: -180px; right: -60px;
}
.blob-2 {
  width: 300px; height: 300px;
  background: #BEF264;
  bottom: -120px; right: 260px;
}
.blob-3 {
  width: 220px; height: 220px;
  background: #22C55E;
  top: 40px; left: -60px;
  opacity: 0.25;
}

.hero-inner {
  position: relative;
  display: flex;
  align-items: center;
  gap: 48px;
  padding: 80px 24px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 텍스트 영역 */
.hero-content { flex: 1; min-width: 0; }

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: rgba(255,255,255,0.12);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  padding: 6px 14px;
  border-radius: var(--r-full);
  margin-bottom: 20px;
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.15);
}
.hero-title {
  font-size: 52px;
  font-weight: 900;
  line-height: 1.15;
  letter-spacing: -1.5px;
  color: #fff;
  margin-bottom: 16px;
}
.hero-sub {
  font-size: 17px;
  color: rgba(255,255,255,0.65);
  line-height: 1.7;
  margin-bottom: 32px;
}
.hero-actions { display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 40px; }

.hero-btn-primary {
  background: var(--primary);
  color: #1a2e10;
  border: none;
  box-shadow: 0 4px 24px rgba(132,204,22,0.45);
}
.hero-btn-primary:hover { background: var(--primary-dark); color: #fff; }

.hero-btn-ghost {
  background: rgba(255,255,255,0.1);
  color: #fff;
  border: 1.5px solid rgba(255,255,255,0.25);
  backdrop-filter: blur(8px);
}
.hero-btn-ghost:hover { background: rgba(255,255,255,0.18); }

/* 통계 */
.hero-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}
.hero-stat-num {
  font-size: 22px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 2px;
}
.hero-stat-label {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  font-weight: 500;
}
.hero-stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255,255,255,0.15);
}

/* 비주얼 영역 */
.hero-visual {
  flex-shrink: 0;
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

/* 메인 카드 */
.hero-main-card {
  width: 200px;
  background: rgba(255,255,255,0.08);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: var(--r-xl);
  overflow: hidden;
  position: relative;
  transition: transform .2s;
  display: block;
}
.hero-main-card:hover { transform: translateY(-4px); }
.placeholder-main { cursor: default; }

.hmc-img {
  width: 100%;
  height: 160px;
  background: rgba(255,255,255,0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.hmc-img img { width: 100%; height: 100%; object-fit: cover; }
.hmc-emoji { font-size: 56px; }
.hmc-body { padding: 14px 16px 16px; }
.hmc-category {
  font-size: 11px;
  font-weight: 700;
  color: var(--primary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.hmc-title {
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  margin: 4px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.hmc-price { font-size: 15px; font-weight: 800; color: #BEF264; }
.hmc-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: var(--primary);
  color: #fff;
  font-size: 10px;
  font-weight: 800;
  padding: 3px 8px;
  border-radius: var(--r-full);
  letter-spacing: 0.5px;
}

/* 사이드 카드 */
.hero-side-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 24px;
}
.hero-side-card {
  width: 170px;
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255,255,255,0.08);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: var(--r-lg);
  padding: 12px 14px;
  transition: background .15s;
  cursor: pointer;
}
.hero-side-card:hover { background: rgba(255,255,255,0.14); }
.placeholder-side { cursor: default; }
.hsc-emoji { font-size: 28px; flex-shrink: 0; }
.hsc-title {
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 2px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.hsc-price { font-size: 12px; font-weight: 700; color: #BEF264; }

.hero-ai-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(132,204,22,0.15);
  border: 1px solid rgba(132,204,22,0.35);
  border-radius: var(--r-lg);
  padding: 10px 14px;
  font-size: 12px;
  color: #BEF264;
  font-weight: 600;
}
.hero-ai-badge span { font-size: 16px; }

.section-title {
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 32px;
  text-align: center;
}

.features { padding: 60px 0; }
.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.feature-card {
  background: var(--surface);
  border-radius: var(--r-lg);
  padding: 28px 24px;
  box-shadow: var(--shadow-sm);
  transition: transform .2s, box-shadow .2s;
}
.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}
.feature-icon { font-size: 36px; display: block; margin-bottom: 14px; }
.feature-card h3 { font-size: 16px; font-weight: 700; margin-bottom: 8px; }
.feature-card p { font-size: 14px; color: var(--text-muted); line-height: 1.6; }

.cta-banner {
  background: linear-gradient(135deg, var(--primary-dark) 0%, var(--primary) 100%);
  border-radius: var(--r-xl);
  padding: 60px;
  text-align: center;
  color: #fff;
}
.cta-banner h2 { font-size: 32px; font-weight: 800; margin-bottom: 12px; }
.cta-banner p { font-size: 16px; opacity: 0.9; margin-bottom: 28px; }
.cta-banner .btn-primary {
  background: #fff;
  color: var(--primary);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}
.cta-banner .btn-primary:hover { background: #f5f5f5; }
</style>

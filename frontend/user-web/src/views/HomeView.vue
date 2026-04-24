<template>
  <div>
    <!-- Hero -->
    <section class="hero">
      <div class="hero-content">
        <p class="hero-badge">🔥 지금 주변에서 거래 중</p>
        <h1 class="hero-title">내 주변 중고거래,<br>이제 더 스마트하게</h1>
        <p class="hero-sub">AI가 당신에게 맞는 물품을 추천해 드려요.<br>믿을 수 있는 판매자와 안전하게 거래하세요.</p>
        <div class="hero-actions">
          <router-link to="/products" class="btn btn-primary btn-lg">지금 둘러보기</router-link>
          <router-link to="/register" class="btn btn-outline btn-lg">무료로 시작하기</router-link>
        </div>
      </div>
      <div class="hero-visual">
        <div class="hero-card">
          <div class="hero-card-img">📱</div>
          <p class="hero-card-title">아이폰 15 Pro</p>
          <p class="hero-card-price">850,000원</p>
        </div>
        <div class="hero-card offset">
          <div class="hero-card-img">👟</div>
          <p class="hero-card-title">나이키 에어맥스</p>
          <p class="hero-card-price">65,000원</p>
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
      <h2>지금 바로 시작해보세요</h2>
      <p>가입하면 AI 추천 서비스를 무료로 이용할 수 있어요</p>
      <router-link to="/register" class="btn btn-primary btn-lg">무료 회원가입</router-link>
    </section>
  </div>
</template>

<script setup>
const features = [
  { icon: '📍', title: '위치 기반 추천', desc: '내 주변 5km 이내 물품을 먼저 보여줘요. 직거래가 훨씬 쉬워져요.' },
  { icon: '🤖', title: 'AI 스마트 추천', desc: '채팅으로 원하는 물품을 말하면 AI가 딱 맞는 것을 찾아줘요.' },
  { icon: '🛡️', title: '신뢰도 검증', desc: '판매자 리뷰를 AI가 분석해서 안전한 거래 상대를 알려줘요.' },
  { icon: '💬', title: '실시간 채팅', desc: '관심 물품이 생기면 바로 채팅으로 가격 흥정을 해보세요.' },
]
</script>

<style scoped>
.hero {
  display: flex;
  align-items: center;
  gap: 48px;
  padding: 60px 0 80px;
}
.hero-content { flex: 1; }
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: var(--primary-light);
  color: var(--primary);
  font-size: 13px;
  font-weight: 600;
  padding: 6px 14px;
  border-radius: var(--r-full);
  margin-bottom: 20px;
}
.hero-title {
  font-size: 48px;
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: -1px;
  margin-bottom: 16px;
}
.hero-sub {
  font-size: 17px;
  color: var(--text-muted);
  line-height: 1.7;
  margin-bottom: 32px;
}
.hero-actions { display: flex; gap: 12px; flex-wrap: wrap; }

.hero-visual {
  flex-shrink: 0;
  display: flex;
  gap: 16px;
  align-items: flex-start;
}
.hero-card {
  background: var(--surface);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-md);
  padding: 20px;
  width: 160px;
  text-align: center;
}
.hero-card.offset { margin-top: 32px; }
.hero-card-img { font-size: 48px; margin-bottom: 8px; }
.hero-card-title { font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.hero-card-price { font-size: 15px; font-weight: 700; color: var(--primary); }

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
  background: linear-gradient(135deg, var(--primary) 0%, #FF8C60 100%);
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

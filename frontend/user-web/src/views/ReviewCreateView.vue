<template>
  <div class="review-wrap">
    <div class="review-header">
      <h1 class="review-title">거래 후기 남기기</h1>
      <p class="review-sub">판매자와의 거래는 어떠셨나요?</p>
    </div>

    <form @submit.prevent="submit" class="review-form">
      <!-- 별점 -->
      <div class="form-section">
        <h3 class="section-title">별점</h3>
        <div class="stars">
          <span
            v-for="n in 5" :key="n"
            class="star"
            :class="{ active: n <= form.rating, hover: n <= hovered }"
            @click="form.rating = n"
            @mouseenter="hovered = n"
            @mouseleave="hovered = 0"
          >★</span>
        </div>
        <p class="rating-label">{{ ratingLabel }}</p>
      </div>

      <!-- AI 키워드 선택 -->
      <div class="form-section">
        <div class="section-title-row">
          <h3 class="section-title">키워드 선택</h3>
          <span class="section-hint">선택하면 AI가 초안을 써드려요</span>
        </div>
        <div class="keyword-groups">
          <div v-for="group in keywordGroups" :key="group.label" class="keyword-group">
            <p class="keyword-group-label">{{ group.label }}</p>
            <div class="keyword-chips">
              <button
                v-for="kw in group.keywords" :key="kw"
                type="button"
                class="keyword-chip"
                :class="{ selected: selectedKeywords.includes(kw) }"
                @click="toggleKeyword(kw)"
              >{{ kw }}</button>
            </div>
          </div>
        </div>
        <button
          v-if="selectedKeywords.length > 0"
          type="button"
          class="btn btn-outline ai-btn"
          :disabled="aiLoading || form.rating === 0"
          @click="generateDraft"
        >
          <span v-if="aiLoading" class="ai-spinner" />
          <span v-else>🤖</span>
          {{ aiLoading ? 'AI 초안 작성 중...' : 'AI로 초안 만들기' }}
        </button>
        <p v-if="form.rating === 0 && selectedKeywords.length > 0" class="ai-hint">
          별점을 먼저 선택해 주세요
        </p>
      </div>

      <!-- 후기 내용 -->
      <div class="form-section">
        <div class="section-title-row">
          <h3 class="section-title">후기 내용</h3>
          <span v-if="isDraft" class="draft-badge">AI 초안</span>
        </div>
        <div class="field">
          <textarea
            v-model="form.content"
            class="input textarea"
            placeholder="거래 경험을 자유롭게 작성해 주세요"
            rows="6"
            @input="isDraft = false"
          />
          <p class="char-count">{{ form.content.length }}자</p>
        </div>
      </div>

      <p v-if="error" class="form-error">{{ error }}</p>

      <div class="form-actions">
        <button type="button" class="btn btn-ghost" @click="router.back()">취소</button>
        <button
          type="submit"
          class="btn btn-primary btn-lg"
          :disabled="loading || form.rating === 0 || !form.content.trim()"
        >
          {{ loading ? '등록 중...' : '후기 등록' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api/index.js'

const route = useRoute()
const router = useRouter()

const form = ref({ rating: 0, content: '' })
const hovered = ref(0)
const loading = ref(false)
const aiLoading = ref(false)
const error = ref('')
const isDraft = ref(false)
const selectedKeywords = ref([])

const keywordGroups = [
  {
    label: '물품 상태',
    keywords: ['사진과 동일해요', '상태가 좋아요', '약간 흠이 있어요', '설명과 달라요']
  },
  {
    label: '거래 경험',
    keywords: ['약속 시간을 잘 지켜요', '친절해요', '빠른 응답', '가격 흥정이 가능해요']
  },
  {
    label: '추천 여부',
    keywords: ['다시 거래하고 싶어요', '강력 추천해요', '보통이에요', '비추천해요']
  }
]

const ratingLabel = computed(() => ({
  0: '별점을 선택해 주세요',
  1: '별로예요', 2: '그저 그래요', 3: '보통이에요', 4: '좋아요', 5: '최고예요!'
}[hovered.value || form.value.rating]))

function toggleKeyword(kw) {
  const idx = selectedKeywords.value.indexOf(kw)
  if (idx === -1) selectedKeywords.value.push(kw)
  else selectedKeywords.value.splice(idx, 1)
}

async function generateDraft() {
  if (form.value.rating === 0 || !selectedKeywords.value.length) return
  aiLoading.value = true
  try {
    const productId = route.query.productId
    let productTitle = `상품 #${productId}`
    try {
      const { data } = await api.get(`/products/${productId}`)
      productTitle = data.title
    } catch {}

    const { data } = await api.post('/ai/review-writer/complete', {
      keywords: selectedKeywords.value,
      product_title: productTitle,
      rating: form.value.rating
    })
    form.value.content = data.review || data.content || ''
    isDraft.value = true
  } catch {
    error.value = 'AI 초안 생성에 실패했어요. 직접 작성해 주세요.'
  } finally {
    aiLoading.value = false
  }
}

async function submit() {
  if (form.value.rating === 0 || !form.value.content.trim()) return
  loading.value = true
  error.value = ''
  try {
    await api.post('/reviews', {
      targetId: Number(route.query.targetId),
      productId: Number(route.query.productId),
      rating: form.value.rating,
      content: form.value.content,
    })
    router.push(`/products/${route.query.productId}`)
  } catch {
    error.value = '후기 등록에 실패했어요. 다시 시도해 주세요.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.review-wrap {
  max-width: 560px;
  margin: 0 auto;
}
.review-header { margin-bottom: 32px; }
.review-title { font-size: 26px; font-weight: 800; margin-bottom: 6px; }
.review-sub { font-size: 14px; color: var(--text-muted); }

.review-form { display: flex; flex-direction: column; gap: 20px; }

.form-section {
  background: var(--surface);
  border-radius: var(--r-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}
.section-title {
  font-size: 15px;
  font-weight: 700;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}
.section-title-row .section-title {
  padding-bottom: 0;
  border-bottom: none;
  flex: 1;
}
.section-hint {
  font-size: 12px;
  color: var(--text-muted);
}
.draft-badge {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: var(--r-full);
  background: var(--primary-light);
  color: var(--primary);
}

/* 별점 */
.stars { display: flex; gap: 8px; }
.star {
  font-size: 40px;
  color: var(--border);
  cursor: pointer;
  transition: color .12s, transform .1s;
  line-height: 1;
}
.star.active, .star.hover { color: #FBBF24; }
.star:hover { transform: scale(1.15); }
.rating-label { font-size: 14px; font-weight: 600; color: var(--text-muted); min-height: 20px; }

/* 키워드 */
.keyword-groups { display: flex; flex-direction: column; gap: 16px; }
.keyword-group { display: flex; flex-direction: column; gap: 8px; }
.keyword-group-label {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.keyword-chips { display: flex; flex-wrap: wrap; gap: 8px; }
.keyword-chip {
  padding: 7px 14px;
  border-radius: var(--r-full);
  font-size: 13px;
  font-weight: 600;
  border: 1.5px solid var(--border);
  background: var(--bg);
  color: var(--text-muted);
  cursor: pointer;
  transition: all .15s;
}
.keyword-chip:hover { border-color: var(--primary); color: var(--primary); }
.keyword-chip.selected {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
}

.ai-btn {
  align-self: flex-start;
  display: flex;
  align-items: center;
  gap: 8px;
}
.ai-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.ai-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid currentColor;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin .7s linear infinite;
  flex-shrink: 0;
}
@keyframes spin { to { transform: rotate(360deg); } }
.ai-hint { font-size: 12px; color: var(--text-muted); margin-top: -8px; }

/* 텍스트에리어 */
.field { display: flex; flex-direction: column; gap: 6px; }
.textarea { resize: vertical; min-height: 140px; line-height: 1.7; }
.char-count {
  font-size: 12px;
  color: var(--text-muted);
  text-align: right;
}

.form-error {
  font-size: 13px;
  color: var(--danger);
  background: #FFF5F5;
  border: 1px solid #FFCDD2;
  border-radius: var(--r-sm);
  padding: 12px 16px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-bottom: 32px;
}
</style>

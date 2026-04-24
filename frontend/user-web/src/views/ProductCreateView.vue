<template>
  <div class="create-wrap">
    <div class="create-header">
      <h1 class="create-title">물품 등록</h1>
      <p class="create-sub">판매할 물품 정보를 입력해 주세요</p>
    </div>

    <form @submit.prevent="submit" class="create-form">
      <!-- 기본 정보 -->
      <div class="form-section">
        <h3 class="section-title">기본 정보</h3>

        <div class="field">
          <label class="field-label">제목 <span class="required">*</span></label>
          <input v-model="form.title" class="input" placeholder="물품 제목을 입력하세요" required />
        </div>

        <div class="field">
          <label class="field-label">카테고리 <span class="required">*</span></label>
          <select v-model="form.category" class="input select-input" required>
            <option value="" disabled>카테고리를 선택하세요</option>
            <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
          </select>
        </div>

        <div class="field">
          <label class="field-label">가격 <span class="required">*</span></label>
          <div class="price-wrap">
            <input
              v-model.number="form.price"
              type="number"
              class="input price-input"
              placeholder="0"
              required
              min="0"
            />
            <span class="price-unit">원</span>
          </div>
        </div>
      </div>

      <!-- 상세 설명 -->
      <div class="form-section">
        <h3 class="section-title">상세 설명</h3>
        <div class="field">
          <label class="field-label">설명</label>
          <textarea
            v-model="form.description"
            class="input textarea"
            placeholder="물품 상태, 구매 시기, 특이사항 등을 적어주세요"
            rows="5"
          />
        </div>
        <div class="field">
          <label class="field-label">이미지 URL</label>
          <input v-model="form.imageUrl" class="input" placeholder="https://..." />
        </div>
      </div>

      <p v-if="error" class="form-error">{{ error }}</p>
      <p v-if="validating" class="form-validating">🤖 AI가 게시글을 검토하는 중...</p>

      <div class="form-actions">
        <router-link to="/products" class="btn btn-ghost">취소</router-link>
        <button type="submit" class="btn btn-primary btn-lg" :disabled="loading">
          {{ loading ? '등록 중...' : '등록하기' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/index.js'

const router = useRouter()
const error = ref('')
const loading = ref(false)
const validating = ref(false)
const categories = ['전자기기', '의류', '가구', '도서', '스포츠', '기타']
const form = ref({
  title: '', description: '', price: 0,
  category: '', imageUrl: '', latitude: 0, longitude: 0
})

async function submit() {
  loading.value = true
  error.value = ''
  try {
    validating.value = true
    await api.post('/ai/policy/validate', {
      content: `${form.value.title} ${form.value.description}`,
      content_type: 'product'
    }).then(({ data }) => {
      if (!data.valid) throw new Error(data.reason)
    }).catch(e => { if (e.message) throw e })
    validating.value = false

    navigator.geolocation.getCurrentPosition(async pos => {
      form.value.latitude = pos.coords.latitude
      form.value.longitude = pos.coords.longitude
      await api.post('/products', form.value)
      router.push('/products')
    }, async () => {
      await api.post('/products', form.value)
      router.push('/products')
    })
  } catch (e) {
    validating.value = false
    error.value = e.message || '등록에 실패했어요. 다시 시도해 주세요.'
    loading.value = false
  }
}
</script>

<style scoped>
.create-wrap {
  max-width: 560px;
  margin: 0 auto;
}
.create-header { margin-bottom: 32px; }
.create-title { font-size: 26px; font-weight: 800; margin-bottom: 6px; }
.create-sub { font-size: 14px; color: var(--text-muted); }

.create-form { display: flex; flex-direction: column; gap: 28px; }

.form-section {
  background: var(--surface);
  border-radius: var(--r-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.section-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 4px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}

.field { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 13px; font-weight: 600; }
.required { color: var(--primary); }

.select-input { cursor: pointer; }
.price-wrap { position: relative; }
.price-input { padding-right: 36px; }
.price-unit {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 14px;
  color: var(--text-muted);
  pointer-events: none;
}

.textarea { resize: vertical; min-height: 120px; line-height: 1.6; }

.form-error {
  font-size: 13px;
  color: var(--danger);
  background: #FFF5F5;
  border: 1px solid #FFCDD2;
  border-radius: var(--r-sm);
  padding: 12px 16px;
}
.form-validating {
  font-size: 13px;
  color: var(--primary);
  background: var(--primary-light);
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

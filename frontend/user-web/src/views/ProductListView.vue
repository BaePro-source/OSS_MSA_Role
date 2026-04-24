<template>
  <div>
    <!-- 검색 바 -->
    <div class="search-bar">
      <div class="search-input-wrap">
        <span class="search-icon">🔍</span>
        <input
          v-model="keyword"
          class="search-input"
          placeholder="어떤 물품을 찾고 계세요?"
          @keyup.enter="search"
        />
      </div>
      <button class="btn btn-primary" @click="search">검색</button>
      <button class="btn btn-outline" @click="loadNearby">
        📍 내 주변
      </button>
    </div>

    <!-- AI 추천 위젯 -->
    <AiChatWidget :products="products" />

    <!-- 카테고리 필터 -->
    <div class="category-scroll">
      <button
        v-for="cat in ['전체', ...categories]" :key="cat"
        class="cat-chip"
        :class="{ active: selectedCategory === cat }"
        @click="filterCategory(cat)"
      >{{ cat }}</button>
    </div>

    <!-- 상품 그리드 -->
    <div class="products-grid" v-if="filtered.length">
      <ProductCard v-for="p in filtered" :key="p.id" :product="p" />
    </div>
    <div v-else class="empty-state">
      <p class="empty-icon">🗂️</p>
      <p class="empty-text">등록된 물품이 없어요</p>
      <router-link to="/products/new" class="btn btn-primary">첫 번째로 등록하기</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api/index.js'
import ProductCard from '../components/ProductCard.vue'
import AiChatWidget from '../components/AiChatWidget.vue'

const products = ref([])
const keyword = ref('')
const selectedCategory = ref('전체')
const categories = ['전자기기', '의류', '가구', '도서', '스포츠', '기타']

const filtered = computed(() => {
  if (selectedCategory.value === '전체') return products.value
  return products.value.filter(p => p.category === selectedCategory.value)
})

function filterCategory(cat) {
  selectedCategory.value = cat
}

async function load() {
  const { data } = await api.get('/products/nearby?lat=36.35&lng=127.38&radius=50')
  products.value = data
}

async function search() {
  if (!keyword.value.trim()) return load()
  const { data } = await api.get(`/products/search?keyword=${keyword.value}`)
  products.value = data
}

async function loadNearby() {
  navigator.geolocation.getCurrentPosition(async pos => {
    const { latitude: lat, longitude: lng } = pos.coords
    const { data } = await api.get(`/products/nearby?lat=${lat}&lng=${lng}&radius=5`)
    products.value = data
  })
}

onMounted(load)
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}
.search-input-wrap {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
}
.search-icon {
  position: absolute;
  left: 14px;
  font-size: 16px;
  pointer-events: none;
}
.search-input {
  width: 100%;
  padding: 12px 16px 12px 42px;
  border: 1.5px solid var(--border);
  border-radius: var(--r-full);
  font-size: 15px;
  background: var(--surface);
  font-family: inherit;
  outline: none;
  transition: border-color .15s;
}
.search-input:focus { border-color: var(--primary); }
.search-input::placeholder { color: #bbb; }

.category-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 4px;
  margin-bottom: 24px;
  scrollbar-width: none;
}
.category-scroll::-webkit-scrollbar { display: none; }
.cat-chip {
  flex-shrink: 0;
  padding: 6px 16px;
  border-radius: var(--r-full);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  border: 1.5px solid var(--border);
  background: var(--surface);
  color: var(--text-muted);
  transition: all .15s;
}
.cat-chip:hover { border-color: var(--primary); color: var(--primary); }
.cat-chip.active {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
  box-shadow: 0 2px 8px rgba(255,107,53,.3);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
@media (max-width: 900px) {
  .products-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 600px) {
  .products-grid { grid-template-columns: repeat(2, 1fr); }
  .search-bar { flex-wrap: wrap; }
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
}
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-text { font-size: 16px; color: var(--text-muted); margin-bottom: 24px; }
</style>

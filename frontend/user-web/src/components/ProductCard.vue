<template>
  <router-link :to="`/products/${product.id}`" class="product-card">
    <div class="card-img-wrap">
      <img
        :src="product.imageUrl || ''"
        :alt="product.title"
        class="card-img"
        @error="e => e.target.style.display='none'"
      />
      <div v-if="!product.imageUrl" class="card-img-placeholder">📦</div>
      <span class="card-badge" :class="badgeClass">{{ badgeText }}</span>
    </div>
    <div class="card-body">
      <p class="card-title">{{ product.title }}</p>
      <p class="card-price">{{ product.price.toLocaleString() }}원</p>
      <p class="card-category">{{ product.category }}</p>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ product: Object })

const badgeText = computed(() => ({
  ON_SALE: '판매중', RESERVED: '예약중', SOLD: '거래완료'
}[props.product.status] || '판매중'))

const badgeClass = computed(() => ({
  ON_SALE: 'badge-sale', RESERVED: 'badge-reserve', SOLD: 'badge-sold'
}[props.product.status] || 'badge-sale'))
</script>

<style scoped>
.product-card {
  display: block;
  background: var(--surface);
  border-radius: var(--r-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: transform .2s, box-shadow .2s;
  cursor: pointer;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.card-img-wrap {
  position: relative;
  width: 100%;
  padding-top: 72%;
  background: var(--bg);
  overflow: hidden;
}
.card-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.card-img-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}
.card-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: var(--r-full);
  font-size: 11px;
  font-weight: 700;
}
.badge-sale    { background: rgba(0,200,83,0.15); color: #2E7D32; }
.badge-reserve { background: rgba(255,179,0,0.15); color: #E65100; }
.badge-sold    { background: rgba(0,0,0,0.08); color: #777; }

.card-body { padding: 14px 16px; }
.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 6px;
}
.card-price {
  font-size: 16px;
  font-weight: 800;
  color: var(--primary);
  margin-bottom: 4px;
}
.card-category {
  font-size: 12px;
  color: var(--text-muted);
}
</style>

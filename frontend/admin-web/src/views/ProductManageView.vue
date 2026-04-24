<template>
  <div>
    <h2>게시글 관리</h2>
    <table style="width:100%;border-collapse:collapse">
      <thead>
        <tr style="background:#f8f9fa">
          <th style="padding:8px;border:1px solid #ddd">ID</th>
          <th style="padding:8px;border:1px solid #ddd">제목</th>
          <th style="padding:8px;border:1px solid #ddd">가격</th>
          <th style="padding:8px;border:1px solid #ddd">상태</th>
          <th style="padding:8px;border:1px solid #ddd">관리</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in products" :key="p.id">
          <td style="padding:8px;border:1px solid #ddd">{{ p.id }}</td>
          <td style="padding:8px;border:1px solid #ddd">{{ p.title }}</td>
          <td style="padding:8px;border:1px solid #ddd">{{ p.price.toLocaleString() }}원</td>
          <td style="padding:8px;border:1px solid #ddd">{{ p.status }}</td>
          <td style="padding:8px;border:1px solid #ddd">
            <button @click="deleteProduct(p.id)" style="color:red">삭제</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/index.js'

const products = ref([])

onMounted(async () => {
  const { data } = await api.get('/products/nearby?lat=36.35&lng=127.38&radius=999')
  products.value = data
})

async function deleteProduct(id) {
  if (!confirm('삭제하시겠습니까?')) return
  await api.delete(`/admin/products/${id}`)
  products.value = products.value.filter(p => p.id !== id)
}
</script>

<template>
  <div class="ai-widget" :class="{ expanded: isOpen }">
    <button class="ai-toggle" @click="isOpen = !isOpen">
      <span class="ai-toggle-icon">🤖</span>
      <span class="ai-toggle-label">AI 추천 받기</span>
      <span class="ai-toggle-arrow">{{ isOpen ? '▲' : '▼' }}</span>
    </button>

    <div v-if="isOpen" class="ai-body">
      <div ref="chatBox" class="ai-messages">
        <div v-if="!history.length" class="ai-empty">
          찾는 물품을 말해주세요. AI가 딱 맞는 걸 찾아드릴게요! 😊
        </div>
        <div v-for="(m, i) in history" :key="i" class="ai-msg" :class="m.role">
          <div class="ai-bubble">{{ m.content }}</div>
        </div>
        <div v-if="recommended.length" class="ai-recommendations">
          <p class="rec-label">추천 물품</p>
          <div class="rec-list">
            <router-link
              v-for="p in recommended" :key="p.id"
              :to="`/products/${p.id}`"
              class="rec-item"
            >
              {{ p.title }}
            </router-link>
          </div>
        </div>
      </div>

      <div class="ai-input-row">
        <input
          v-model="message"
          @keyup.enter="ask"
          class="input ai-input"
          placeholder="어떤 물품을 찾고 있나요?"
          :disabled="loading"
        />
        <button class="btn btn-primary btn-sm ai-send" @click="ask" :disabled="loading">
          {{ loading ? '...' : '전송' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import api from '../api/index.js'

const props = defineProps({ products: Array })
const isOpen = ref(false)
const message = ref('')
const history = ref([])
const recommended = ref([])
const loading = ref(false)
const chatBox = ref(null)

async function ask() {
  if (!message.value.trim()) return
  history.value.push({ role: 'user', content: message.value })
  loading.value = true
  try {
    const { data } = await api.post('/ai/recommend/chat', {
      message: message.value,
      products: props.products
    })
    recommended.value = data.recommendations
    history.value.push({ role: 'ai', content: `${data.recommendations.length}개 물품을 추천해 드렸어요!` })
  } catch {
    history.value.push({ role: 'ai', content: 'AI 추천 서비스에 일시적인 오류가 발생했어요.' })
  } finally {
    loading.value = false
    message.value = ''
    nextTick(() => chatBox.value?.scrollTo(0, chatBox.value.scrollHeight))
  }
}
</script>

<style scoped>
.ai-widget {
  background: var(--surface);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-sm);
  border: 1.5px solid var(--border);
  margin-bottom: 24px;
  overflow: hidden;
  transition: box-shadow .2s;
}
.ai-widget.expanded { box-shadow: var(--shadow-md); }

.ai-toggle {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  background: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  border: none;
}
.ai-toggle:hover { background: var(--primary-light); }
.ai-toggle-icon { font-size: 20px; }
.ai-toggle-label { flex: 1; text-align: left; }
.ai-toggle-arrow { font-size: 11px; color: var(--text-muted); }

.ai-body { border-top: 1px solid var(--border); }

.ai-messages {
  max-height: 200px;
  overflow-y: auto;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.ai-empty {
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
  padding: 8px 0;
}

.ai-msg { display: flex; }
.ai-msg.user { justify-content: flex-end; }
.ai-msg.ai { justify-content: flex-start; }

.ai-bubble {
  max-width: 80%;
  padding: 8px 14px;
  border-radius: var(--r-lg);
  font-size: 13px;
  line-height: 1.5;
}
.ai-msg.user .ai-bubble {
  background: var(--primary);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.ai-msg.ai .ai-bubble {
  background: var(--bg);
  color: var(--text);
  border-bottom-left-radius: 4px;
}

.ai-recommendations {
  margin-top: 4px;
  padding: 12px 14px;
  background: var(--primary-light);
  border-radius: var(--r-md);
}
.rec-label { font-size: 12px; font-weight: 700; color: var(--primary); margin-bottom: 8px; }
.rec-list { display: flex; flex-wrap: wrap; gap: 6px; }
.rec-item {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  background: var(--surface);
  border: 1px solid var(--primary);
  color: var(--primary);
  border-radius: var(--r-full);
  transition: all .15s;
}
.rec-item:hover { background: var(--primary); color: #fff; }

.ai-input-row {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
  background: var(--bg);
}
.ai-input { margin: 0; }
.ai-send { flex-shrink: 0; }
.ai-send:disabled { opacity: 0.5; cursor: not-allowed; }
</style>

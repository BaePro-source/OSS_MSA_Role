<template>
  <div class="chat-layout">
    <!-- 채팅방 목록 -->
    <aside class="room-list">
      <div class="room-list-header">
        <h3 class="room-list-title">채팅</h3>
        <span class="room-count">{{ rooms.length }}</span>
      </div>

      <div v-if="roomsLoading" class="room-skeleton-list">
        <div v-for="i in 3" :key="i" class="room-skeleton" />
      </div>

      <div v-else-if="!rooms.length" class="room-empty">진행 중인 채팅이 없어요</div>

      <div
        v-for="room in enrichedRooms" :key="room.id"
        class="room-item"
        :class="{ active: currentRoom?.id === room.id }"
        @click="selectRoom(room)"
      >
        <div class="room-avatar">{{ room.productEmoji }}</div>
        <div class="room-info">
          <div class="room-name-row">
            <p class="room-name">{{ room.productTitle }}</p>
            <span class="room-time">{{ room.lastTime }}</span>
          </div>
          <div class="room-meta-row">
            <span class="room-partner">{{ room.partnerNickname }}</span>
            <span v-if="room.lastMsg" class="room-last-msg">{{ room.lastMsg }}</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- 메시지 영역 -->
    <div class="chat-main">
      <template v-if="currentRoom">
        <div class="chat-header">
          <div class="chat-header-info">
            <div class="chat-header-avatar">{{ currentRoom.productEmoji }}</div>
            <div>
              <p class="chat-header-title">{{ currentRoom.productTitle }}</p>
              <p class="chat-header-sub">{{ currentRoom.partnerNickname }}</p>
            </div>
            <span v-if="productStatus === 'SOLD'" class="sold-badge">거래완료</span>
          </div>
          <div class="chat-header-actions">
            <button
              v-if="myId === currentRoom.sellerId && productStatus !== 'SOLD'"
              class="btn btn-ghost btn-sm close-btn"
              @click="closeTrade"
            >
              🔒 거래 종료
            </button>
            <router-link
              v-if="myId === currentRoom.buyerId && productStatus === 'SOLD'"
              :to="`/reviews/new?targetId=${currentRoom.sellerId}&productId=${currentRoom.productId}`"
              class="btn btn-primary btn-sm"
            >
              ✏️ 리뷰 작성
            </router-link>
          </div>
        </div>

        <div ref="msgBox" class="msg-area">
          <div v-if="messagesLoading" class="msg-loading">
            <div class="spinner" />
          </div>
          <div v-else-if="!messages.length" class="msg-empty">
            <p>대화를 시작해 보세요 👋</p>
          </div>
          <template v-else>
            <template v-for="(m, i) in messages" :key="m.id">
              <!-- 날짜 구분선 -->
              <div v-if="showDateDivider(i)" class="date-divider">
                <span>{{ formatDate(m.sentAt) }}</span>
              </div>
              <div class="msg-row" :class="{ mine: m.senderId === myId }">
                <div class="msg-col">
                  <div class="msg-bubble">{{ m.content }}</div>
                  <span class="msg-time">{{ formatTime(m.sentAt) }}</span>
                </div>
              </div>
            </template>
          </template>
        </div>

        <div class="msg-input-bar">
          <input
            v-model="input"
            @keyup.enter="send"
            class="input msg-input"
            placeholder="메시지를 입력하세요..."
            :disabled="productStatus === 'SOLD'"
          />
          <button
            class="btn btn-primary send-btn"
            @click="send"
            :disabled="!input.trim() || productStatus === 'SOLD'"
          >
            전송
          </button>
        </div>
      </template>

      <div v-else class="chat-placeholder">
        <p class="placeholder-icon">💬</p>
        <p class="placeholder-text">왼쪽에서 채팅방을 선택하세요</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import api from '../api/index.js'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
const route = useRoute()
const rooms = ref([])
const roomMeta = ref({})   // roomId → { productTitle, partnerNickname, lastMsg, lastTime }
const currentRoom = ref(null)
const messages = ref([])
const input = ref('')
const msgBox = ref(null)
const myId = auth.user?.userId
const productStatus = ref(null)
const roomsLoading = ref(true)
const messagesLoading = ref(false)

let stompClient = null
const productCache = {}
const userCache = {}

const CATEGORY_EMOJI = {
  '전자기기': '📱', '의류': '👕', '가구': '🪑',
  '도서': '📚', '스포츠': '⚽', '기타': '📦'
}

async function fetchProduct(id) {
  if (!productCache[id]) {
    const { data } = await api.get(`/products/${id}`)
    productCache[id] = data
  }
  return productCache[id]
}

async function fetchUser(id) {
  if (!userCache[id]) {
    try {
      const { data } = await api.get(`/auth/users/${id}`)
      userCache[id] = data
    } catch {
      userCache[id] = { nickname: `사용자 #${id}` }
    }
  }
  return userCache[id]
}

async function enrichRoom(room) {
  const partnerId = room.buyerId === myId ? room.sellerId : room.buyerId
  const [product, partner] = await Promise.all([
    fetchProduct(room.productId),
    fetchUser(partnerId)
  ])
  return {
    ...room,
    productTitle: product.title || `상품 #${room.productId}`,
    productEmoji: CATEGORY_EMOJI[product.category] || '📦',
    partnerNickname: partner.nickname,
    lastMsg: null,
    lastTime: formatTimeShort(room.createdAt)
  }
}

const enrichedRooms = computed(() =>
  rooms.value.map(r => roomMeta.value[r.id] ?? r)
)

onMounted(async () => {
  const { data } = await api.get('/chat/rooms')
  rooms.value = data
  roomsLoading.value = false

  // 각 방 메타데이터 비동기 로딩
  await Promise.all(data.map(async room => {
    const meta = await enrichRoom(room)
    roomMeta.value = { ...roomMeta.value, [room.id]: meta }
  }))

  connectWs(() => {
    if (route.query.roomId) {
      const room = roomMeta.value[route.query.roomId]
        || data.find(r => r.id == route.query.roomId)
      if (room) selectRoom(room)
    }
  })
})

onUnmounted(() => {
  stompClient?.deactivate()
})

function connectWs(onConnected) {
  const token = localStorage.getItem('accessToken')
  stompClient = new Client({
    webSocketFactory: () => new SockJS('/ws/chat'),
    connectHeaders: { Authorization: `Bearer ${token}` },
    onConnect: () => onConnected?.(),
  })
  stompClient.activate()
}

function subscribeRoom(roomId) {
  stompClient?.subscribe(`/topic/room/${roomId}`, frame => {
    const msg = JSON.parse(frame.body)
    messages.value.push(msg)
    // 마지막 메시지 미리보기 업데이트
    if (roomMeta.value[roomId]) {
      roomMeta.value[roomId].lastMsg = msg.content
      roomMeta.value[roomId].lastTime = formatTimeShort(msg.sentAt)
    }
    nextTick(() => scrollToBottom())
  })
}

async function selectRoom(room) {
  currentRoom.value = roomMeta.value[room.id] ?? room
  messagesLoading.value = true
  messages.value = []

  const [msgRes, productRes] = await Promise.all([
    api.get(`/chat/rooms/${room.id}/messages`),
    api.get(`/products/${room.productId}`)
  ])
  messages.value = msgRes.data
  productStatus.value = productRes.data.status

  // 마지막 메시지 미리보기 반영
  if (msgRes.data.length && roomMeta.value[room.id]) {
    const last = msgRes.data[msgRes.data.length - 1]
    roomMeta.value[room.id].lastMsg = last.content
    roomMeta.value[room.id].lastTime = formatTimeShort(last.sentAt)
  }

  messagesLoading.value = false
  subscribeRoom(room.id)
  nextTick(() => scrollToBottom())
}

async function closeTrade() {
  if (!currentRoom.value) return
  await api.patch(`/products/${currentRoom.value.productId}/status?status=SOLD`)
  productStatus.value = 'SOLD'
}

function send() {
  if (!input.value.trim() || !currentRoom.value || !stompClient?.connected) return
  stompClient.publish({
    destination: '/app/chat.send',
    body: JSON.stringify({
      roomId: currentRoom.value.id,
      senderId: myId,
      content: input.value
    })
  })
  input.value = ''
}

function scrollToBottom() {
  msgBox.value?.scrollTo({ top: msgBox.value.scrollHeight, behavior: 'smooth' })
}

function showDateDivider(index) {
  if (index === 0) return true
  const prev = messages.value[index - 1]
  const curr = messages.value[index]
  return formatDate(prev.sentAt) !== formatDate(curr.sentAt)
}

function formatDate(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  return d.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })
}

function formatTime(ts) {
  if (!ts) return ''
  return new Date(ts).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
}

function formatTimeShort(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  const now = new Date()
  const diffDays = Math.floor((now - d) / 86400000)
  if (diffDays === 0) return d.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
  if (diffDays === 1) return '어제'
  if (diffDays < 7) return `${diffDays}일 전`
  return d.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
}
</script>

<style scoped>
.chat-layout {
  display: flex;
  height: calc(100vh - 128px);
  background: var(--surface);
  border-radius: var(--r-xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

/* ── 채팅방 목록 ── */
.room-list {
  width: 280px;
  flex-shrink: 0;
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  background: var(--bg);
  overflow-y: auto;
}
.room-list-header {
  position: sticky;
  top: 0;
  z-index: 1;
  background: var(--bg);
  padding: 20px 16px 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-bottom: 1px solid var(--border);
}
.room-list-title { font-size: 16px; font-weight: 800; }
.room-count {
  background: var(--primary);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: var(--r-full);
}
.room-empty {
  padding: 40px 16px;
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
}

/* 스켈레톤 */
.room-skeleton-list { display: flex; flex-direction: column; gap: 0; }
.room-skeleton {
  height: 72px;
  border-bottom: 1px solid var(--border);
  background: linear-gradient(90deg, var(--border) 25%, #f0f0f0 50%, var(--border) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
}
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.room-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  border-bottom: 1px solid var(--border);
  transition: background .12s;
  min-width: 0;
}
.room-item:hover { background: var(--surface); }
.room-item.active { background: var(--primary-light); }

.room-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--surface);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
}

.room-info { flex: 1; min-width: 0; }
.room-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  margin-bottom: 3px;
}
.room-name {
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}
.room-time {
  font-size: 11px;
  color: var(--text-muted);
  flex-shrink: 0;
}
.room-meta-row {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}
.room-partner {
  font-size: 11px;
  color: var(--primary);
  font-weight: 600;
  flex-shrink: 0;
}
.room-last-msg {
  font-size: 11px;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ── 채팅 메인 ── */
.chat-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }

.chat-header {
  padding: 14px 20px;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.chat-header-info {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}
.chat-header-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: var(--primary-light);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.chat-header-title {
  font-size: 15px;
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.chat-header-sub { font-size: 12px; color: var(--text-muted); margin-top: 1px; }
.chat-header-actions { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }

.sold-badge {
  font-size: 11px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: var(--r-full);
  background: rgba(0,0,0,0.08);
  color: #777;
}
.close-btn { color: var(--danger); border-color: var(--danger); }

/* ── 메시지 ── */
.msg-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px 20px 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.msg-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  padding: 40px 0;
}
.spinner {
  width: 28px;
  height: 28px;
  border: 3px solid var(--border);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin .7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.msg-empty {
  margin: auto;
  font-size: 14px;
  color: var(--text-muted);
}

/* 날짜 구분선 */
.date-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 12px 0 8px;
  color: var(--text-muted);
  font-size: 12px;
  font-weight: 600;
}
.date-divider::before,
.date-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border);
}

.msg-row {
  display: flex;
  margin-bottom: 6px;
}
.msg-row.mine { justify-content: flex-end; }

.msg-col {
  display: flex;
  flex-direction: column;
  gap: 3px;
  max-width: 68%;
}
.msg-row.mine .msg-col { align-items: flex-end; }

.msg-bubble {
  padding: 10px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  background: var(--bg);
  color: var(--text);
  border-bottom-left-radius: 4px;
  word-break: break-word;
}
.msg-row.mine .msg-bubble {
  background: var(--primary);
  color: #fff;
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 4px;
}
.msg-time {
  font-size: 11px;
  color: var(--text-muted);
  padding: 0 4px;
}

/* ── 입력창 ── */
.msg-input-bar {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
  flex-shrink: 0;
}
.msg-input { margin: 0; }
.send-btn {
  flex-shrink: 0;
  padding: 10px 20px;
}
.send-btn:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }

/* ── 플레이스홀더 ── */
.chat-placeholder {
  margin: auto;
  text-align: center;
}
.placeholder-icon { font-size: 48px; margin-bottom: 12px; }
.placeholder-text { font-size: 14px; color: var(--text-muted); }
</style>

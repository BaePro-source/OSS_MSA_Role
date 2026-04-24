<template>
  <div class="chat-layout">
    <!-- 채팅방 목록 -->
    <aside class="room-list">
      <div class="room-list-header">
        <h3 class="room-list-title">채팅</h3>
        <span class="room-count">{{ rooms.length }}</span>
      </div>
      <div v-if="!rooms.length" class="room-empty">진행 중인 채팅이 없어요</div>
      <div
        v-for="room in rooms" :key="room.id"
        class="room-item"
        :class="{ active: currentRoom?.id === room.id }"
        @click="selectRoom(room)"
      >
        <div class="room-avatar">🛍️</div>
        <div class="room-info">
          <p class="room-name">상품 #{{ room.productId }}</p>
          <p class="room-sub">방 #{{ room.id }}</p>
        </div>
      </div>
    </aside>

    <!-- 메시지 영역 -->
    <div class="chat-main">
      <template v-if="currentRoom">
        <div class="chat-header">
          <p class="chat-header-title">상품 #{{ currentRoom.productId }}</p>
          <p class="chat-header-sub">방 #{{ currentRoom.id }}</p>
        </div>

        <div ref="msgBox" class="msg-area">
          <div v-if="!messages.length" class="msg-empty">
            <p>대화를 시작해 보세요 👋</p>
          </div>
          <div
            v-for="m in messages" :key="m.id"
            class="msg-row"
            :class="{ mine: m.senderId === myId }"
          >
            <div class="msg-bubble">{{ m.content }}</div>
          </div>
        </div>

        <div class="msg-input-bar">
          <input
            v-model="input"
            @keyup.enter="send"
            class="input msg-input"
            placeholder="메시지 입력..."
          />
          <button class="btn btn-primary send-btn" @click="send">
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
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import Stomp from 'stompjs'
import SockJS from 'sockjs-client'
import api from '../api/index.js'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
const route = useRoute()
const rooms = ref([])
const currentRoom = ref(null)
const messages = ref([])
const input = ref('')
const msgBox = ref(null)
const myId = auth.user?.userId

let stompClient = null

onMounted(async () => {
  const { data } = await api.get('/chat/rooms')
  rooms.value = data

  if (route.query.roomId) {
    const room = data.find(r => r.id == route.query.roomId)
    if (room) selectRoom(room)
  }

  connectWs()
})

function connectWs() {
  const socket = new SockJS('/ws/chat')
  stompClient = Stomp.over(socket)
  stompClient.connect({}, () => {
    if (currentRoom.value) subscribeRoom(currentRoom.value.id)
  })
}

function subscribeRoom(roomId) {
  stompClient?.subscribe(`/topic/room/${roomId}`, frame => {
    const msg = JSON.parse(frame.body)
    messages.value.push(msg)
    nextTick(() => msgBox.value?.scrollTo(0, msgBox.value.scrollHeight))
  })
}

async function selectRoom(room) {
  currentRoom.value = room
  const { data } = await api.get(`/chat/rooms/${room.id}/messages`)
  messages.value = data
  subscribeRoom(room.id)
  nextTick(() => msgBox.value?.scrollTo(0, msgBox.value.scrollHeight))
}

function send() {
  if (!input.value.trim() || !currentRoom.value) return
  stompClient?.send('/app/chat.send', {}, JSON.stringify({
    roomId: currentRoom.value.id,
    senderId: myId,
    content: input.value
  }))
  input.value = ''
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

/* 채팅방 목록 */
.room-list {
  width: 260px;
  flex-shrink: 0;
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  background: var(--bg);
}
.room-list-header {
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
  padding: 24px 16px;
  font-size: 13px;
  color: var(--text-muted);
  text-align: center;
}
.room-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  border-bottom: 1px solid var(--border);
  transition: background .1s;
}
.room-item:hover { background: var(--surface); }
.room-item.active { background: var(--primary-light); }
.room-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--surface);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
}
.room-name { font-size: 13px; font-weight: 600; margin-bottom: 2px; }
.room-sub { font-size: 11px; color: var(--text-muted); }

/* 메시지 영역 */
.chat-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }

.chat-header {
  padding: 14px 20px;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}
.chat-header-title { font-size: 15px; font-weight: 700; margin-bottom: 2px; }
.chat-header-sub { font-size: 12px; color: var(--text-muted); }

.msg-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.msg-empty {
  margin: auto;
  font-size: 14px;
  color: var(--text-muted);
}

.msg-row { display: flex; }
.msg-row.mine { justify-content: flex-end; }
.msg-bubble {
  max-width: 68%;
  padding: 10px 16px;
  border-radius: var(--r-lg);
  font-size: 14px;
  line-height: 1.5;
  background: var(--bg);
  color: var(--text);
  border-bottom-left-radius: 4px;
}
.msg-row.mine .msg-bubble {
  background: var(--primary);
  color: #fff;
  border-bottom-left-radius: var(--r-lg);
  border-bottom-right-radius: 4px;
}

.msg-input-bar {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid var(--border);
  flex-shrink: 0;
}
.msg-input { margin: 0; }
.send-btn { flex-shrink: 0; }

.chat-placeholder {
  margin: auto;
  text-align: center;
}
.placeholder-icon { font-size: 48px; margin-bottom: 12px; }
.placeholder-text { font-size: 14px; color: var(--text-muted); }
</style>

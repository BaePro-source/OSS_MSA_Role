package com.oss.chat.controller;

import com.oss.chat.dto.ChatMessageDto;
import com.oss.chat.entity.ChatMessage;
import com.oss.chat.entity.ChatRoom;
import com.oss.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket: /app/chat.send → /topic/room/{roomId}
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDto dto) {
        ChatMessage saved = chatService.save(dto.getRoomId(), dto.getSenderId(), dto.getContent());
        messagingTemplate.convertAndSend("/topic/room/" + dto.getRoomId(), saved);
    }

    // REST: 채팅방 생성 또는 조회
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> getOrCreateRoom(
        @RequestHeader("X-User-Id") Long userId,
        @RequestBody Map<String, Long> body
    ) {
        Long productId = body.get("productId");
        Long sellerId = body.get("sellerId");
        return ResponseEntity.ok(chatService.getOrCreateRoom(productId, userId, sellerId));
    }

    // REST: 내 채팅방 목록
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> myRooms(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(chatService.getRooms(userId));
    }

    // REST: 채팅 내역 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> messages(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getMessages(roomId));
    }
}

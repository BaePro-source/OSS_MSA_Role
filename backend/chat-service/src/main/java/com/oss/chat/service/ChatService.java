package com.oss.chat.service;

import com.oss.chat.entity.ChatMessage;
import com.oss.chat.entity.ChatRoom;
import com.oss.chat.repository.ChatMessageRepository;
import com.oss.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoom getOrCreateRoom(Long productId, Long buyerId, Long sellerId) {
        return chatRoomRepository
            .findByProductIdAndBuyerIdAndSellerId(productId, buyerId, sellerId)
            .orElseGet(() -> chatRoomRepository.save(
                ChatRoom.builder()
                    .productId(productId)
                    .buyerId(buyerId)
                    .sellerId(sellerId)
                    .build()
            ));
    }

    public List<ChatRoom> getRooms(Long userId) {
        return chatRoomRepository.findByBuyerIdOrSellerIdOrderByCreatedAtDesc(userId, userId);
    }

    public List<ChatMessage> getMessages(Long roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    public ChatMessage save(Long roomId, Long senderId, String content) {
        return chatMessageRepository.save(
            ChatMessage.builder()
                .roomId(roomId)
                .senderId(senderId)
                .content(content)
                .build()
        );
    }
}

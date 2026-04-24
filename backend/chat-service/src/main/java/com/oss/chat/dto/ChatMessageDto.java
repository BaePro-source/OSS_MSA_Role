package com.oss.chat.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private Long roomId;
    private Long senderId;
    private String content;
}

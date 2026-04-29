package com.oss.chat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Chat Service API")
                .description("채팅방 생성·조회 및 실시간 메시지(STOMP WebSocket) 서비스")
                .version("1.0.0"));
    }
}

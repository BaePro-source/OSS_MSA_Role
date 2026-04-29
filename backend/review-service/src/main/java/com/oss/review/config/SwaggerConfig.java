package com.oss.review.config;

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
                .title("Review Service API")
                .description("리뷰 작성, 평점 조회 및 저신뢰 사용자 탐지 서비스")
                .version("1.0.0"));
    }
}

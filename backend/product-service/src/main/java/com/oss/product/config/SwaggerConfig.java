package com.oss.product.config;

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
                .title("Product Service API")
                .description("상품 등록, 조회, 수정, 삭제 및 위치 기반 검색 서비스")
                .version("1.0.0"));
    }
}

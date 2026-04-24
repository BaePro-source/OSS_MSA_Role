package com.oss.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RestTemplate restTemplate;

    @Value("${services.product}")
    private String productServiceUrl;

    @Value("${services.review}")
    private String reviewServiceUrl;

    // 게시글 삭제 (관리자 전용)
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long productId,
        @RequestHeader("X-User-Role") String role
    ) {
        checkAdmin(role);
        restTemplate.delete(productServiceUrl + "/api/products/" + productId);
        return ResponseEntity.noContent().build();
    }

    // 낮은 평점 사용자 조회
    @GetMapping("/users/low-rated")
    public ResponseEntity<?> getLowRatedUsers(
        @RequestHeader("X-User-Role") String role,
        @RequestParam(defaultValue = "2.0") double threshold
    ) {
        checkAdmin(role);
        Object result = restTemplate.getForObject(
            reviewServiceUrl + "/api/reviews/low-rated?threshold=" + threshold,
            Object.class
        );
        return ResponseEntity.ok(result);
    }

    // 상태 확인
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "admin-service"));
    }

    private void checkAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalStateException("관리자 권한이 필요합니다.");
        }
    }
}

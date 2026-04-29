package com.oss.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "관리자", description = "관리자 전용 - 게시글 삭제, 사용자 관리, 통계 조회")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RestTemplate restTemplate;

    @Value("${services.product}")
    private String productServiceUrl;

    @Value("${services.review}")
    private String reviewServiceUrl;

    @Value("${services.auth}")
    private String authServiceUrl;

    @Operation(summary = "통계 조회", description = "전체 상품 수, 전체 사용자 수 반환 (ADMIN 전용)")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats(@RequestHeader("X-User-Role") String role) {
        checkAdmin(role);
        Object[] products = restTemplate.getForObject(productServiceUrl + "/api/products", Object[].class);
        Object[] users    = restTemplate.getForObject(authServiceUrl + "/api/auth/users", Object[].class);

        Map<String, Object> result = new HashMap<>();
        result.put("totalProducts", products != null ? products.length : 0);
        result.put("totalUsers",    users    != null ? users.length    : 0);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "전체 상품 목록 조회 (ADMIN 전용)")
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(@RequestHeader("X-User-Role") String role) {
        checkAdmin(role);
        Object result = restTemplate.getForObject(productServiceUrl + "/api/products", Object.class);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시글 강제 삭제 (ADMIN 전용)")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long productId,
        @RequestHeader("X-User-Role") String role
    ) {
        checkAdmin(role);
        restTemplate.delete(productServiceUrl + "/api/products/" + productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 사용자 목록 조회 (ADMIN 전용)")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("X-User-Role") String role) {
        checkAdmin(role);
        Object result = restTemplate.getForObject(authServiceUrl + "/api/auth/users", Object.class);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "사용자 밴/밴 해제 (ADMIN 전용)")
    @PatchMapping("/users/{id}/ban")
    public ResponseEntity<Void> toggleBan(
        @PathVariable Long id,
        @RequestHeader("X-User-Role") String role
    ) {
        checkAdmin(role);
        restTemplate.exchange(
            authServiceUrl + "/api/auth/users/" + id + "/ban",
            HttpMethod.PATCH, null, Void.class
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "저신뢰 사용자 조회 (ADMIN 전용)", description = "threshold 이하 평균 평점 사용자 반환")
    @GetMapping("/users/low-rated")
    public ResponseEntity<?> getLowRatedUsers(
        @RequestHeader("X-User-Role") String role,
        @RequestParam(defaultValue = "2.0") double threshold
    ) {
        checkAdmin(role);
        Object result = restTemplate.getForObject(
            reviewServiceUrl + "/api/reviews/low-rated?threshold=" + threshold, Object.class);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "서비스 상태 확인")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "admin-service"));
    }

    private void checkAdmin(String role) {
        if (!"ADMIN".equals(role)) throw new IllegalStateException("관리자 권한이 필요합니다.");
    }
}

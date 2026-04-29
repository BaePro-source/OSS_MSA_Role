package com.oss.product.controller;

import com.oss.product.dto.ProductRequest;
import com.oss.product.entity.Product;
import com.oss.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품", description = "상품 등록, 조회, 수정, 삭제 및 위치 기반 검색")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "전체 상품 목록 조회")
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(summary = "상품 등록", description = "인증 필요 (X-User-Id 헤더)")
    @PostMapping
    public ResponseEntity<Product> create(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.ok(productService.create(userId, req));
    }

    @Operation(summary = "위치 기반 상품 조회", description = "위도(lat), 경도(lng), 반경(radius km) 기준 검색")
    @GetMapping("/nearby")
    public ResponseEntity<List<Product>> nearby(
        @RequestParam double lat,
        @RequestParam double lng,
        @RequestParam(defaultValue = "5") double radius
    ) {
        return ResponseEntity.ok(productService.findNearby(lat, lng, radius));
    }

    @Operation(summary = "키워드 검색")
    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.search(keyword));
    }

    @Operation(summary = "상품 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "판매자별 상품 목록 조회")
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Product>> bySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(productService.findBySeller(sellerId));
    }

    @Operation(summary = "상품 수정", description = "본인만 수정 가능 (X-User-Id 헤더)")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.ok(productService.update(id, userId, req));
    }

    @Operation(summary = "상품 삭제", description = "본인만 삭제 가능 (X-User-Id 헤더)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId
    ) {
        productService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품 상태 변경", description = "SALE / RESERVED / SOLD")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Product> updateStatus(
        @PathVariable Long id,
        @RequestParam Product.Status status
    ) {
        return ResponseEntity.ok(productService.updateStatus(id, status));
    }
}

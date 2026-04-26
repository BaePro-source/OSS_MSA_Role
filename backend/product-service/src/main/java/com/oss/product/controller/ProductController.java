package com.oss.product.controller;

import com.oss.product.dto.ProductRequest;
import com.oss.product.entity.Product;
import com.oss.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping
    public ResponseEntity<Product> create(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.ok(productService.create(userId, req));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Product>> nearby(
        @RequestParam double lat,
        @RequestParam double lng,
        @RequestParam(defaultValue = "5") double radius
    ) {
        return ResponseEntity.ok(productService.findNearby(lat, lng, radius));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.search(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Product>> bySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(productService.findBySeller(sellerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.ok(productService.update(id, userId, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestHeader("X-User-Id") Long userId
    ) {
        productService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Product> updateStatus(
        @PathVariable Long id,
        @RequestParam Product.Status status
    ) {
        return ResponseEntity.ok(productService.updateStatus(id, status));
    }
}
